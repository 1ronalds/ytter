package api.ytter.backend.service;

import api.ytter.backend.database_model.NotificationEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.NotificationRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.model.NotificationData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationData> getAllNotifications(String username) {
        // iegūst visus paziņojumus
        UserEntity user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        List<NotificationEntity> notifications = notificationRepository.findAllByUser(user.getId());
        notifications.stream()
                .filter(n -> !n.getRead())
                .forEach(n -> n.setRead(true)); // atzīmē visus skatītos komentārus kā lasītus

        notificationRepository.saveAll(notifications);

        deleteOldNotifications(user); // izdzēš vecos (vecāki par 50) paziņojumus

        return notifications.stream().map(notificationEntity -> new NotificationData(
                        notificationEntity.getDescription(),
                        notificationEntity.getLink(),
                        notificationEntity.getRead(),
                        notificationEntity.getTimestamp())).toList();
    }

    public List<NotificationData> getUnreadNotifications(String username) {
        // atgriež nelasītos paziņojumus
        UserEntity user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        List<NotificationEntity> notificationEntities = notificationRepository.findByUserAndNotRead(user.getId());

        List<NotificationData> notifications = notificationEntities.stream()
                .map(notificationEntity -> new NotificationData(
                        notificationEntity.getDescription(),
                        notificationEntity.getLink(),
                        notificationEntity.getRead(),
                        notificationEntity.getTimestamp()))
                .toList();

        notificationEntities.forEach(notificationEntity -> {
            notificationEntity.setRead(true); // atzīmē kā izlasītus
            notificationRepository.save(notificationEntity);
        });

        deleteOldNotifications(user);

        return notifications;
    }

    public Integer getUnreadNotificationCount(String username) {
        // iegūst nelasīto paziņojumu skaitu
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        return Math.toIntExact(notificationRepository.getUnreadCount(user.getId()));
    }

    private void deleteOldNotifications(UserEntity userEntity) {
        // izdzēš paziņojumus kas vecāki par 50
        notificationRepository.findAllByUser(userEntity.getId()).stream().skip(50).forEach(notificationRepository::delete);
    }
}
