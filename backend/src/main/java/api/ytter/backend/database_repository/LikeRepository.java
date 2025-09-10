package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.LikeEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    @Query("SELECT l FROM LikeEntity l WHERE l.user = :user AND l.post = :post")
    Optional<LikeEntity> findByUserAndPost(@Param("user") UserEntity user, @Param("post") PostEntity post);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM likes WHERE post_id = :post_id", nativeQuery = true)
    void deleteByPostId(@Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM likes WHERE comment_id = :comment_id", nativeQuery = true)
    void deleteByCommentId(@Param("comment_id") Long commentId);

    @Query("SELECT l FROM LikeEntity l WHERE l.user = :user AND l.comment = :comment")
    Optional<LikeEntity> findByUserAndComment(@Param("user") UserEntity user, @Param("comment") CommentEntity comment);

}
