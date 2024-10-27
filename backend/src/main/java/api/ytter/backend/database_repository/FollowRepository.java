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

    @Query("SELECT f.follower FROM FollowEntity f WHERE f.following.username = :followingUsername")
    List<UserEntity> findFollowersByFollowingUsername(@Param("followingUsername") String followingUsername);

    @Query("SELECT f.following FROM FollowEntity f WHERE f.follower.username = :followerUsername")
    List<UserEntity> findFollowingByFollowerUsername(@Param("followerUsername") String followerUsername);

}
