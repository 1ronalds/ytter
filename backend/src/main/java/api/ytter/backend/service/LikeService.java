package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.LikeEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.CommentRepository;
import api.ytter.backend.database_repository.LikeRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public void likePostAdd(String username, Long postId){
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(RuntimeException::new);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);

        if(likeRepository.findByUserAndPost(userEntity, postEntity).isPresent()){
            throw new RuntimeException();
        }
        postEntity.increaseLikeCount();
        postRepository.save(postEntity);

        likeRepository.save(new LikeEntity(userEntity, postEntity));
    }

    public void likePostRemove(String username, Long postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(RuntimeException::new);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        LikeEntity likeEntity = likeRepository.findByUserAndPost(userEntity, postEntity).orElseThrow(RuntimeException::new);
        postEntity.decreaseLikeCount();
        postRepository.save(postEntity);
        likeRepository.delete(likeEntity);
    }

    public void likeCommentAdd(String username, Long commentId){
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);

        if(likeRepository.findByUserAndComment(userEntity, commentEntity).isPresent()){
            throw new RuntimeException();
        }
        commentEntity.increaseLikeCount();
        commentRepository.save(commentEntity);

        likeRepository.save(new LikeEntity(userEntity, commentEntity));
    }

    public void likeCommentRemove(String username, Long commentId){
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        LikeEntity likeEntity = likeRepository.findByUserAndComment(userEntity, commentEntity).orElseThrow(RuntimeException::new);
        commentEntity.decreaseLikeCount();

        likeRepository.delete(likeEntity);
    }
}
