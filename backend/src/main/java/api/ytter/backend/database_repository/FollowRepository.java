package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.FollowEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    @Query("SELECT f FROM FollowEntity f WHERE f.follower.username = :followerUsername AND f.following.username = :followingUsername")
    FollowEntity findByFollowerAndFollowing(@Param("followerUsername") String followerUsername,
                                            @Param("followingUsername") String followingUsername);


    @Query(value = "SELECT * FROM follow WHERE follower_id = :followerId", nativeQuery = true)
    List<FollowEntity> findFollowingFromUsername(@Param("followerId") Long followerId);

    @Query(value = "SELECT * FROM follow WHERE following_id = :followingId", nativeQuery = true)
    List<FollowEntity> findFollowersToUsername(@Param("followingId") Long followingId);

}
