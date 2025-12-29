package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// klase apraksta funkcijas, kas komunicē ar datubāzi. Šīs ir manuāli veidotās funkcijas un satur SQL vaicājumus.

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = """
            SELECT *
            FROM comments
            WHERE root_post = :rootPostId AND reply_to_comment IS NULL
            ORDER BY timestamp_ DESC
            """, nativeQuery = true)
    List<CommentEntity> findAllAsReplyToPost(@Param("rootPostId") Long postId);

    @Query(value = "SELECT * FROM comments WHERE reply_to_comment = :commentId ORDER BY timestamp_ DESC", nativeQuery = true)
    List<CommentEntity> findAllByReplyToComment_Id(Long commentId);

    @Query(value = "SELECT * FROM comments WHERE root_post = :root_post ORDER BY timestamp_ DESC", nativeQuery = true)
    List<CommentEntity> findAllByRootPostId(@Param("root_post") Long root_post);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE reported = true
            ORDER BY timestamp_ DESC
            """, nativeQuery = true)
    List<CommentEntity> findAllByReported();
}

