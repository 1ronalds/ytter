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

    public List<CommentData> getCommentsToPost(Long postId){
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
                        likeRepository.findByUserAndComment(commentEntity.getUser(), commentEntity).isPresent()
                        ))
                .toList();
    }


    public List<CommentData> getCommentsToComment(Long commentId){
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
                        likeRepository.findByUserAndComment(commentEntity.getUser(), commentEntity).isPresent()
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
        notificationEntity.setDescription("Your comment/post has a reply");
        notificationEntity.setUser(commentEntity.getReplyToComment() == null ?
                commentEntity.getRootPost().getUser() : commentEntity.getReplyToComment().getUser());
        notificationEntity.setLink(commentEntity.getReplyToComment() == null ?
                "https://ytter.lv/comment/to-post/".concat(String.valueOf(commentEntity.getRootPost().getId()))
                : "https://ytter.lv/comment/to-comment/".concat(String.valueOf(commentEntity.getReplyToComment().getId())));
        notificationEntity.setRead(false);
        notificationEntity.setTimestamp(new Date());
        notificationRepository.save(notificationEntity);

        return getCommentById(commentData.getCommentId());
    }

    public void deleteComment(String username, Long commentId) {
        CommentEntity deletableComment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidDataException("Deletable comment doesnt exist"));
        if (deletableComment.getUser().getUsername().equals(username) || userRepository.findByUsername(username).get().getIsAdmin()) {
            if (!commentRepository.findByReplyToCommentId(deletableComment.getId()).isEmpty()) {
                deletableComment.setComment("[deleted]");
                deletableComment.setUser(null);
                commentRepository.save(deletableComment);
            } else {
                commentRepository.delete(deletableComment);
            }
        } else {
            throw new InvalidDataException("Not your comment and not admin");
        }
    }
}
