package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_repository.*;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<CommentData> getCommentsToPost(Long postId){
        return commentRepository.findAllAsReplyToPost(postId)
                .stream()
                .map(commentEntity -> new CommentData(
                        commentEntity.getId(),
                        commentEntity.getUser().getId(),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment().getId(),
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
                        commentEntity.getUser().getId(),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment().getId(),
                        commentEntity.getLikeCount(),
                        commentEntity.getReplyCount(),
                        commentEntity.getComment(),
                        commentEntity.getTimestamp(),
                        likeRepository.findByUserAndComment(commentEntity.getUser(), commentEntity).isPresent()
                )).toList();
    }


    public CommentData createComment(CommentData commentData) {
        Long postId;
        Random random = new Random();
        do {
            postId = random.nextLong();
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
        commentEntity.setComment(commentData.getComment());
        commentEntity.setRootPost(postRepository.findById(commentData.getRootPostId()).orElseThrow(RuntimeException::new));
        commentEntity.setId(postId);
        commentEntity.setTimestamp(new Date());
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
                "/comment/to-post/".concat(String.valueOf(commentEntity.getRootPost().getId()))
                : "/comment/to-comment/".concat(String.valueOf(commentEntity.getReplyToComment().getId())));
        notificationEntity.setRead(false);
        notificationEntity.setTimestamp(new Date());
        notificationRepository.save(notificationEntity);

        return commentData;
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
