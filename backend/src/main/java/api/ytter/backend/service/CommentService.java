package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_repository.*;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.CommentData;
import api.ytter.backend.model.PostData;
import api.ytter.backend.model.ProfilePublicData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    private CommentData getCommentById(Long commentId){
        return commentRepository.findById(commentId).map(commentEntity -> new CommentData(commentEntity.getId(),
                new ProfilePublicData(commentEntity.getUser().getUsername(), commentEntity.getUser().getName()),
                commentEntity.getRootPost().getId(),
                commentEntity.getReplyToComment() != null ? commentEntity.getReplyToComment().getId() : null,
                commentEntity.getLikeCount(), commentEntity.getReplyCount(),
                commentEntity.getComment(), commentEntity.getTimestamp(), false)).orElseThrow(()-> new InvalidDataException("Comment with this id doesnt exist"));
    }

    public List<CommentData> getCommentsToPost(String username, Long postId){
        return commentRepository.findAllAsReplyToPost(postId)
                .stream()
                .map(commentEntity -> new CommentData(
                        commentEntity.getId(),
                        new ProfilePublicData(commentEntity.getUser().getUsername(), commentEntity.getUser().getName()),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment() != null ? commentEntity.getReplyToComment().getId() : null,
                        commentEntity.getLikeCount(),
                        commentEntity.getReplyCount(),
                        commentEntity.getComment(),
                        commentEntity.getTimestamp(),
                        username != null ? likeRepository.findByUserAndComment(userRepository.findByUsername(username).orElseThrow(), commentEntity).isPresent() : null
                        ))
                .toList();
    }


    public List<CommentData> getCommentsToComment(String username, Long commentId){
        return commentRepository.findAllByReplyToComment_Id(commentId)
                .stream()
                .map(commentEntity -> new CommentData(
                        commentEntity.getId(),
                        new ProfilePublicData(commentEntity.getUser().getUsername(), commentEntity.getUser().getName()),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment() != null ? commentEntity.getReplyToComment().getId() : null,
                        commentEntity.getLikeCount(),
                        commentEntity.getReplyCount(),
                        commentEntity.getComment(),
                        commentEntity.getTimestamp(),
                        username != null ? likeRepository.findByUserAndComment(userRepository.findByUsername(username).orElseThrow(), commentEntity).isPresent() : null
                )).toList();
    }


    public CommentData createComment(CommentData commentData, String username) {
        Long postId;
        Random random = new Random();
        do {
            postId = Math.abs(random.nextLong());
        } while (postRepository.existsById(postId));
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setReplyCount(0L);
        CommentEntity replyTo = null;
        PostEntity rootPost = null;
        if(commentData.getReplyToCommentId() != null){
            replyTo = commentRepository.findById(commentData.getReplyToCommentId()).orElseThrow(RuntimeException::new);
            commentEntity.setReplyToComment(replyTo);
            replyTo.increaseReplyCount();
        } else {
            rootPost = postRepository.findById(commentData.getRootPostId()).orElseThrow(RuntimeException::new);
            commentEntity.setReplyToComment(null);
            rootPost.increaseReplyCount();
        }
        commentEntity.setUser(userRepository.findByUsername(username).orElseThrow(RuntimeException::new));
        commentEntity.setComment(commentData.getComment());
        commentEntity.setRootPost(postRepository.findById(commentData.getRootPostId()).orElseThrow(RuntimeException::new));
        commentEntity.setId(postId);
        commentEntity.setTimestamp(new Date());
        commentEntity.setReported(false);
        commentEntity.setLikeCount(0L);
        commentEntity.setReplyCount(0L);
        if(commentData.getReplyToCommentId() != null){
            commentRepository.save(replyTo);
        };
        commentRepository.save(commentEntity);
        commentData.setCommentId(postId);

        NotificationEntity notificationEntity = new NotificationEntity();
        String replyToText = "";
        Boolean post = null;
        if(replyTo != null){
            replyToText = replyTo.getComment();
            post = false;
        } else {
            replyToText = rootPost.getText();
            post = true;
        }
        notificationEntity.setDescription((post ? "post" : "comment") + " \"" + replyToText + "\" has reply: \"" + commentData.getComment() + "\"");
        notificationEntity.setUser(commentEntity.getReplyToComment() == null ?
                commentEntity.getRootPost().getUser() : commentEntity.getReplyToComment().getUser());
        notificationEntity.setLink("11");
        notificationEntity.setRead(false);
        notificationEntity.setTimestamp(new Date());
        notificationRepository.save(notificationEntity);

        return getCommentById(commentData.getCommentId());
    }

    public void deleteComment(String username, Long commentId) {
        CommentEntity deletableComment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidDataException("Deletable comment doesnt exist"));
        if (deletableComment.getUser().getUsername().equals(username) || userRepository.findByUsername(username).get().getIsAdmin()) {
            if(deletableComment.getReplyToComment() == null){
                PostEntity postEntity = deletableComment.getRootPost();
                postEntity.decreaseReplyCount();
                postRepository.save(postEntity);
            } else {
                CommentEntity commentEntity = commentRepository.findById(deletableComment.getReplyToComment().getId()).orElseThrow();
                commentEntity.decreaseReplyCount();
                commentRepository.save(commentEntity);
            }
            deleteCommentAndChildren(commentId);
        } else {
            throw new InvalidDataException("Not your comment and not admin");
        }
    }

    @Transactional
    public void deleteCommentAndChildren(Long commentId){
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow();
        for(CommentEntity child: commentRepository.findAllByReplyToComment_Id(commentId)){
            deleteCommentAndChildren(child.getId());
        }
        likeRepository.deleteByCommentId(comment.getId());
        commentRepository.delete(comment);
    }
}
