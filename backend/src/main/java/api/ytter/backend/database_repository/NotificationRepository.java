package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUser(UserEntity userEntity);

    @Query(value = """
            SELECT * FROM notifications
            WHERE user_id = :userId
            AND read = false
            ORDER BY timestamp DESC
            """, nativeQuery = true)
    List<NotificationEntity> findByUserAndNotRead(@Param("userEntity") UserEntity userEntity);

    @Query(value = """
            SELECT * FROM notifications
            WHERE user_id = :userId
            AND read = true
            ORDER BY timestamp DESC
            """, nativeQuery = true)
    List<NotificationEntity> findByUserAndRead(@Param("userEntity") UserEntity userEntity);
}
