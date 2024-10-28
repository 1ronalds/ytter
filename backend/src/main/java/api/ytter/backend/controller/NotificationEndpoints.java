package api.ytter.backend.controller;

import api.ytter.backend.model.NotificationData;
import api.ytter.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationEndpoints {
    private final NotificationService notificationService;

    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationData>> getUnreadNotifications(@RequestAttribute String username){
        return new ResponseEntity<>(notificationService.getUnreadNotifications(username), HttpStatus.OK);
    }

    @GetMapping("/notifications/all")
    public ResponseEntity<List<NotificationData>> getAllNotifications(@RequestAttribute String username){
        // Only 50
        return new ResponseEntity<>(notificationService.getAllNotifications(username), HttpStatus.OK);
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationCount(@RequestAttribute String username){
        return new ResponseEntity<>(notificationService.getUnreadNotificationCount(username), HttpStatus.OK);
    }
}