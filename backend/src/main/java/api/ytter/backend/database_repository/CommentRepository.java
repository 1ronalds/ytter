package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<CommentEntity> findAllByUser(String user, Pageable pageable);

    @Query(value = "DELETE FROM comments WHERE rootPost = :postId", nativeQuery = true)
    void deleteAllByRootPostId(Long postId);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE replyToComment.id = :replyToCommentId
            """, nativeQuery = true)
    List<CommentEntity> findByReplyToCommentId(Long replyToCommentId);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE reported = true
            """, nativeQuery = true)
    List<CommentEntity> findAllByReported();
}

