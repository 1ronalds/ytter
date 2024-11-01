package api.ytter.backend.controller;

import api.ytter.backend.model.PostData;
import api.ytter.backend.model.ReyeetPostData;
import api.ytter.backend.service.ReyeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReyeetEndpoints {
    private final ReyeetService reyeetService;
    @PostMapping("/posts/{postId}/ry")
    public ResponseEntity<Void> addReyeet(@RequestAttribute String username, @PathVariable Long postId){
        reyeetService.addReyeet(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/ry")
    public ResponseEntity<Void> deleteReyeet(@RequestAttribute String username, @PathVariable Long postId){
        reyeetService.deleteReyeet(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/posts/following-reyeet-feed")
    ResponseEntity<List<ReyeetPostData>> getFollowingFeed(@RequestAttribute String username,
                                                          @RequestParam Integer limit,
                                                          @RequestParam Integer offset){
        return new ResponseEntity<List<ReyeetPostData>> (reyeetService.getFollowingReyeetFeed(username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/reyeets/{username}")
    ResponseEntity<List<ReyeetPostData>> getReyeetsByUser(@RequestAttribute String username,
                                                          @RequestParam Integer limit,
                                                          @RequestParam Integer offset){
        return new ResponseEntity<>(reyeetService.getReyeetsByUser(username, limit, offset), HttpStatus.OK);
    }
}
