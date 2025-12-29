package api.ytter.backend.service;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.ReyeetEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.LikeRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.ReyeetRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.PostData;
import api.ytter.backend.model.ProfilePublicData;
import api.ytter.backend.model.ReyeetPostData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReyeetService {
    private final ReyeetRepository reyeetRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public void addReyeet(String username, Long postId) {
        // pievieno rejītu, izveidojot rejīta entītiju un palielinot rejītu skaitu publikācijas datos
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new InvalidDataException("Post doesnt exist"));

        if (reyeetRepository.findByUserAndPost(userEntity, postEntity).isEmpty()) {
            ReyeetEntity reyeetEntity = new ReyeetEntity();
            reyeetEntity.setUser(userEntity);
            reyeetEntity.setPost(postEntity);
            reyeetRepository.save(reyeetEntity);
            postEntity.setReyeetCount(postEntity.getReyeetCount()+1);
            postRepository.save(postEntity);
        } else {
            throw new RuntimeException();
        }
    }

    public void deleteReyeet(String username, Long postId) {
        // dzēš rejītu
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new InvalidDataException("Post doesnt exist"));
        ReyeetEntity reyeetEntity = reyeetRepository.findByUserAndPost(userEntity, postEntity).orElseThrow(RuntimeException::new);
        reyeetRepository.delete(reyeetEntity);
        postEntity.setReyeetCount(postEntity.getReyeetCount()-1);
        postRepository.save(postEntity);
    }

    public List<ReyeetPostData> getFollowingReyeetFeed(String username, Integer limit, Integer offset) {
        // iegūst sarakstu ar rejītiem no lietotājiem, kam tiek sekots
        Pageable pageable = PageRequest.of(offset, limit);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        List<ReyeetEntity> reyeetEntityList = reyeetRepository.findReyeetsByUserFollowing(userEntity.getId(), pageable);
        return reyeetEntityList.stream().map(reyeetEntity -> new ReyeetPostData(
                reyeetEntity.getPost().getId().toString(),
                new ProfilePublicData(reyeetEntity.getPost().getUser().getUsername(), reyeetEntity.getPost().getUser().getName()),
                reyeetEntity.getPost().getImageId(),
                reyeetEntity.getPost().getReplyCount(),
                reyeetEntity.getPost().getLikeCount(),
                reyeetEntity.getPost().getReyeetCount(),
                reyeetEntity.getPost().getText(),
                reyeetEntity.getPost().getTimestamp(),
                likeRepository.findByUserAndPost(userRepository.findByUsername(username).orElseThrow(), reyeetEntity.getPost()).isPresent(),
                reyeetRepository.findByUserAndPost(userRepository.findByUsername(username).orElseThrow(), reyeetEntity.getPost()).isPresent(),
                reyeetEntity.getUser().getName()
        )).toList();
    }

    public List<ReyeetPostData> getReyeetsByUser(String requester, String username, Integer limit, Integer offset) {
        // iegūst rejītu sarakstu, ko veicis kāds lietotājs
        Pageable pageable = PageRequest.of(offset, limit);
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        List<ReyeetEntity> reyeetEntityList = reyeetRepository.findReyeetsByUser(userEntity, pageable);
        return reyeetEntityList.stream().map(reyeetEntity -> new ReyeetPostData(
                reyeetEntity.getPost().getId().toString(),
                new ProfilePublicData(reyeetEntity.getPost().getUser().getUsername(), reyeetEntity.getPost().getUser().getName()),
                reyeetEntity.getPost().getImageId(),
                reyeetEntity.getPost().getReplyCount(),
                reyeetEntity.getPost().getLikeCount(),
                reyeetEntity.getPost().getReyeetCount(),
                reyeetEntity.getPost().getText(),
                reyeetEntity.getPost().getTimestamp(),
                requester != null ? likeRepository.findByUserAndPost(userRepository.findByUsername(requester).orElseThrow(), reyeetEntity.getPost()).isPresent() : null,
                requester != null ? reyeetRepository.findByUserAndPost(userRepository.findByUsername(requester).orElseThrow(), reyeetEntity.getPost()).isPresent() : null,
                reyeetEntity.getUser().getName()
        )).toList();
    }
}
