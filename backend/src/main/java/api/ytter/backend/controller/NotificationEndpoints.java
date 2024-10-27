package api.ytter.backend.controller;

import api.ytter.backend.model.NotificationData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationEndpoints {

    @GetMapping("/notifications/unread")
    public ResponseEntity<Integer> getUnreadNotifications(@RequestAttribute String username){
        return null;
    }

    @GetMapping("/notifications/all")
    public ResponseEntity<List<NotificationData>> getAllNotifications(@RequestAttribute String username){
        return null;
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<NotificationData> getUnreadNotifications(){
        return null;
    }

}
