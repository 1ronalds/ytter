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
        UserEntity user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        List<NotificationEntity> unreadNotifications = notificationRepository.findByUserAndNotRead(user.getId());
        List<NotificationEntity> readNotifications = notificationRepository.findByUserAndRead(user.getId());
        readNotifications.forEach(notificationEntity -> {
            notificationEntity.setRead(true);
            notificationRepository.save(notificationEntity);
        });
        List<NotificationEntity> allNotifications = new LinkedList<>();
        allNotifications.addAll(unreadNotifications);
        allNotifications.addAll(readNotifications);

        deleteOldNotifications(user);

        return allNotifications.stream().map(notificationEntity -> new NotificationData(
                        notificationEntity.getDescription(),
                        notificationEntity.getLink(),
                        notificationEntity.getRead(),
                        notificationEntity.getTimestamp())).toList();
    }

    public List<NotificationData> getUnreadNotifications(String username) {
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
            notificationEntity.setRead(true);
            notificationRepository.save(notificationEntity);
        });

        deleteOldNotifications(user);

        return notifications;
    }

    public Integer getUnreadNotificationCount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        return Math.toIntExact(notificationRepository.findByUser(user)
                .stream()
                .filter(NotificationEntity::getRead)
                .count());
    }

    private void deleteOldNotifications(UserEntity userEntity) {
        notificationRepository.findByUser(userEntity).stream().skip(50).forEach(notificationRepository::delete);
    }
}
