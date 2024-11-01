package api.ytter.backend.controller;

import api.ytter.backend.model.ProfilePublicData;
import api.ytter.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FollowEndpoints {
    private final FollowService followService;

    @PostMapping("/profile/{username}/follow")
    public ResponseEntity<Void> followUser(@RequestAttribute("username") String followerUsername, @PathVariable("username") String followUsername){
        followService.followUser(followerUsername, followUsername);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/profile/{username}/follow")
    public ResponseEntity<Void> unFollowUser(@RequestAttribute("username") String followerUsername, @PathVariable("username") String unfollowUsername){
        followService.unFollowUser(followerUsername, unfollowUsername);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/profile/{username}/following")
    public ResponseEntity<List<ProfilePublicData>> getFollowing(@PathVariable String username){
        return new ResponseEntity<List<ProfilePublicData>>(followService.getFollowingList(username),HttpStatus.OK);
    }

    @GetMapping("/profile/{username}/followers")
    public ResponseEntity<List<ProfilePublicData>> getFollowers(@PathVariable String username){
        return new ResponseEntity<List<ProfilePublicData>>(followService.getFollowerList(username),HttpStatus.OK);
    }
}
