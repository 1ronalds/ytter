package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// klase apraksta funkcijas, kas komunicē ar datubāzi. Šīs ir manuāli veidotās funkcijas un satur SQL vaicājumus.

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query(value = "SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = false ORDER BY timestamp_ DESC ", nativeQuery = true)
    Long getUnreadCount(@Param("userId")Long userId);

    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId ORDER BY timestamp_ DESC", nativeQuery = true)
    List<NotificationEntity> findAllByUser(@Param("userId") Long userId);

    List<NotificationEntity> findByUser(UserEntity userEntity);

    @Query(value = """
            SELECT * FROM notifications
            WHERE user_id = :userId
            AND is_read = false
            ORDER BY timestamp_ DESC
            """, nativeQuery = true)
    List<NotificationEntity> findByUserAndNotRead(@Param("userId") Long userID);

    @Query(value = """
            SELECT * FROM notifications
            WHERE user_id = :userId
            AND is_read = true
            ORDER BY timestamp_ DESC
            """, nativeQuery = true)
    List<NotificationEntity> findByUserAndRead(@Param("userId") Long userId);
}
