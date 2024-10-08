package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_repository.CommentRepository;
import api.ytter.backend.database_repository.LikeRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.model.CommentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public List<CommentData> getCommentsToPost(Long postId){
        return commentRepository.findAllAsReplyToPost(postId)
                .stream()
                .map(commentEntity -> new CommentData(
                        commentEntity.getId(),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment().getId(),
                        commentEntity.getReyeetCount(),
                        commentEntity.getLikeCount(),
                        commentEntity.getReplyCount(),
                        commentEntity.getComment(),
                        likeRepository.findByUserAndComment(commentEntity.getUser(), commentEntity).isPresent(),
                        true
                        ))
                .toList();
    }

    public List<CommentData> getCommentsToComment(Long commentId){
        return commentRepository.findAllByReplyToComment_Id(commentId)
                .stream()
                .map(commentEntity -> new CommentData(
                        commentEntity.getId(),
                        commentEntity.getRootPost().getId(),
                        commentEntity.getReplyToComment().getId(),
                        commentEntity.getReyeetCount(),
                        commentEntity.getLikeCount(),
                        commentEntity.getReplyCount(),
                        commentEntity.getComment(),
                        likeRepository.findByUserAndComment(commentEntity.getUser(), commentEntity).isPresent(),
                        true
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
        if(commentData.getReplyToCommentId() != null){commentRepository.save(replyTo);};
        commentRepository.save(commentEntity);
        commentData.setCommentId(postId);
        return commentData;
    }
}
