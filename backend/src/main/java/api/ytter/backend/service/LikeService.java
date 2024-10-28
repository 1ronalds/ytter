package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.LikeEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.CommentRepository;
import api.ytter.backend.database_repository.LikeRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.exception.exception_types.InvalidDataException;
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
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(()-> new InvalidDataException("Post doesnt exist"));
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);

        if(likeRepository.findByUserAndPost(userEntity, postEntity).isPresent()){
            throw new RuntimeException();
        }
        postEntity.increaseLikeCount();
        postRepository.save(postEntity);

        likeRepository.save(new LikeEntity(userEntity, postEntity));
    }

    public void likePostRemove(String username, Long postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(()-> new InvalidDataException("Post doesnt exist"));
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        LikeEntity likeEntity = likeRepository.findByUserAndPost(userEntity, postEntity).orElseThrow(()-> new InvalidDataException("Post isnt liked"));
        postEntity.decreaseLikeCount();
        postRepository.save(postEntity);
        likeRepository.delete(likeEntity);
    }

    public void likeCommentAdd(String username, Long commentId){
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(()-> new InvalidDataException("Comment doesnt exist"));
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);

        if(likeRepository.findByUserAndComment(userEntity, commentEntity).isPresent()){
            throw new InvalidDataException("Comment is already liked");
        }
        commentEntity.increaseLikeCount();
        commentRepository.save(commentEntity);

        likeRepository.save(new LikeEntity(userEntity, commentEntity));
    }

    public void likeCommentRemove(String username, Long commentId){
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(()-> new InvalidDataException("Comment doesnt exist"));
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        LikeEntity likeEntity = likeRepository.findByUserAndComment(userEntity, commentEntity).orElseThrow(()-> new InvalidDataException("Comment isnt liked"));
        commentEntity.decreaseLikeCount();

        likeRepository.delete(likeEntity);
    }
}
