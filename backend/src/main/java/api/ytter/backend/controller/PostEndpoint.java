package api.ytter.backend.controller;


import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.model.PostData;
import api.ytter.backend.other.FileObject;
import api.ytter.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostEndpoint {
    private final PostService postService;

    @GetMapping("/posts/profile/{username}")
    ResponseEntity<List<PostData>> getPostsByUsername(@PathVariable String username,
                                                      @RequestParam Integer limit,
                                                      @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>>(postService.getPostsByUsername(username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/by-me")
    ResponseEntity<List<PostData>> getPostsByMe(@RequestAttribute String username,
                                                @RequestParam Integer limit,
                                                @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getPostsByUsername(username, limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/following-feed")
    ResponseEntity<List<PostData>> getFollowingFeed(@RequestAttribute String username,
                                                    @RequestParam Integer limit,
                                                    @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>> (postService.getFollowingFeed(username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/top/this-week")
    ResponseEntity<List<PostData>> getTopPostsThisWeek(@RequestParam Integer limit,
                                                       @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getTopPostsPast7Days(limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/top/this-month")
    ResponseEntity<List<PostData>> getTopPostsThisMonth(@RequestParam Integer limit,
                                                        @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getTopPostsPast30Days(limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/new")
    ResponseEntity<List<PostData>> getNewPosts(@RequestParam Integer limit,
                                               @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>>(postService.getNewPosts(limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/by-id/{post-id}")
    ResponseEntity<PostData> getPostById(@PathVariable("post-id") Long postId){
        return new ResponseEntity<PostData>(postService.getPostById(postId), HttpStatus.OK);
    }

    @PostMapping("/posts/upload")
    ResponseEntity<PostData> uploadPost(@RequestAttribute String username,
                                          @RequestPart(value = "file", required = false) MultipartFile image,
                                          @RequestPart(value = "post") PostData post){
        return new ResponseEntity<>(postService.uploadPost(image, post, username), HttpStatus.OK);
    }

    @GetMapping("/posts/images/{image-id}")
    ResponseEntity<byte[]> getImage(@PathVariable("image-id") Long imageId){
        FileObject image = postService.getImage(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMimeType()));
        headers.setContentDispositionFormData("inline", image.getFilename());
        return new ResponseEntity<>(image.getFile(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{post-id}")
    ResponseEntity<Void> deletePost(@RequestAttribute String username, @PathVariable("post-id") Long postId){
        postService.deletePost(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
