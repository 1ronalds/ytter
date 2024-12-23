package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = """
            SELECT *
            FROM comments
            WHERE root_post = :rootPostId AND reply_to_comment IS NULL
            """, nativeQuery = true)
    List<CommentEntity> findAllAsReplyToPost(@Param("rootPostId") Long postId);

    List<CommentEntity> findAllByReplyToComment_Id(Long commentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comments WHERE root_post = :postId", nativeQuery = true)
    void deleteAllByRootPostId(Long postId);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE reply_to_comment = :replyToCommentId
            """, nativeQuery = true)
    List<CommentEntity> findByReplyToCommentId(Long replyToCommentId);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE reported = true
            """, nativeQuery = true)
    List<CommentEntity> findAllByReported();
}

