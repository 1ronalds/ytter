package api.ytter.backend.service;

import api.ytter.backend.database_model.FollowEntity;
import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.FollowRepository;
import api.ytter.backend.database_repository.NotificationRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.ProfilePublicData;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public void followUser(String follower, String following){
        FollowEntity followEntity = new FollowEntity();
        followEntity.setFollower(userRepository.findByUsername(follower).orElseThrow(RuntimeException::new));
        UserEntity followingEntity = userRepository.findByUsername(following).orElseThrow(()-> new InvalidDataException("User doesnt exist"));
        followEntity.setFollowing(followingEntity);
        followRepository.save(followEntity);

        NotificationEntity notificationEntity= new NotificationEntity();
        notificationEntity.setDescription(follower.concat(" has started following you."));
        notificationEntity.setUser(followingEntity);
        notificationEntity.setLink("ytter.lv/posts/profile/".concat(follower));
        notificationEntity.setRead(false);
        notificationEntity.setTimestamp(new Date());
        notificationRepository.save(notificationEntity);
    }

    public void unFollowUser(String follower, String unfollowing){
        followRepository.delete(followRepository.findByFollowerAndFollowing(follower, unfollowing));
    }

    public List<ProfilePublicData> getFollowerList(String username){
        return followRepository.findFollowersToUsername(userRepository.findByUsername(username)
                .orElseThrow(()->new InvalidDataException("Username doesnt exist")).getId()).stream()
                .map((followEntity -> new ProfilePublicData(
                followEntity.getFollower().getUsername(),
                followEntity.getFollower().getName()
        ))).toList();
    }

    public List<ProfilePublicData> getFollowingList(String username){
        return followRepository.findFollowingFromUsername(userRepository.findByUsername(username)
                .orElseThrow(()->new InvalidDataException("Username doesnt exist"))
                .getId()).stream().map((followEntity -> new ProfilePublicData(
                followEntity.getFollowing().getUsername(),
                followEntity.getFollowing().getName()
        ))).toList();
    }
}
