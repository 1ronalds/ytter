package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
