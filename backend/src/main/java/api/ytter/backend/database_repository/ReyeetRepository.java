package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.ReyeetEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReyeetRepository extends JpaRepository<ReyeetEntity, Long> {

    @Query("SELECT l FROM ReyeetEntity l WHERE l.user = :user AND l.post = :post")
    Optional<ReyeetEntity> findByUserAndPost(@Param("user") UserEntity user, @Param("post") PostEntity post);

    @Query(value = """
            SELECT r.*
            FROM posts p
            JOIN reyeet r ON p.id = r.post_id
            JOIN follow f ON r.user_id = f.following_id
            WHERE f.follower_id = :userId
            ORDER BY r.id DESC
            """, nativeQuery = true)
    List<ReyeetEntity> findReyeetsByUserFollowing(@Param("userId") Long userId, Pageable pageable);

    List<ReyeetEntity> findReyeetsByUser(UserEntity userEntity, Pageable pageable);
}
