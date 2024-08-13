package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.rootPost.id = :rootPostId AND c.replyToComment IS NULL")
    List<CommentEntity> findAllAsReplyToPost(@Param("rootPostId") Long rootPostId);


    List<CommentEntity> findAllByReplyToComment_Id(Long commentId);

}
