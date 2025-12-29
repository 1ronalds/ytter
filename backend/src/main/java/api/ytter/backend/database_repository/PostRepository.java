package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// klase apraksta funkcijas, kas komunicē ar datubāzi. Šīs ir manuāli veidotās funkcijas un satur SQL vaicājumus.

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query(value = """
            SELECT * 
            FROM posts
            WHERE timestamp_ BETWEEN :startDate AND :endDate 
            ORDER BY like_count DESC, timestamp_ DESC
            """,
            nativeQuery = true)
    List<PostEntity> findAllByDateRangeSortedByLikes(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);

    Optional<PostEntity> findByImageId(Long imageId);

    @Query(value = """
            SELECT *
            FROM posts
            WHERE reported = true
            """, nativeQuery = true)
    List<PostEntity> findAllByReported();

    List<PostEntity> findAllByOrderByTimestamp_Desc(Pageable pageable);

    List<PostEntity> findByUser(UserEntity user, Pageable pageable);

    @Query(value = """
            SELECT p.*
            FROM posts p
            JOIN follow f ON p.author = f.following_id
            WHERE f.follower_id = :userId
            ORDER BY p.timestamp_ DESC
            """,
            nativeQuery = true)
    List<PostEntity> findAllPostsByUsersFollowing(Long userId, Pageable pageable);
}
