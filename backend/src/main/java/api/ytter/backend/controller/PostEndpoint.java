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

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

@RequiredArgsConstructor
@RestController
public class PostEndpoint {
    private final PostService postService;

    @GetMapping("/posts/profile/{username}")
    ResponseEntity<List<PostData>> getPostsByUsername(@RequestAttribute(name="username", required = false) String requester,
                                                      @PathVariable String username,
                                                      @RequestParam Integer limit,
                                                      @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>>(postService.getPostsByUsername(requester, username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/by-me")
    ResponseEntity<List<PostData>> getPostsByMe(@RequestAttribute String username,
                                                @RequestParam Integer limit,
                                                @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getPostsByUsername(username, username, limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/following-feed")
    ResponseEntity<List<PostData>> getFollowingFeed(@RequestAttribute String username,
                                                    @RequestParam Integer limit,
                                                    @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>> (postService.getFollowingFeed(username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/top/this-week")
    ResponseEntity<List<PostData>> getTopPostsThisWeek(@RequestAttribute(name="username", required = false) String requester,
                                                       @RequestParam Integer limit,
                                                       @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getTopPostsPast7Days(requester, limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/top/this-month")
    ResponseEntity<List<PostData>> getTopPostsThisMonth(@RequestAttribute(name="username", required = false) String requester,
                                                        @RequestParam Integer limit,
                                                        @RequestParam Integer offset){
        return new ResponseEntity<>(postService.getTopPostsPast30Days(requester, limit, offset), HttpStatus.OK);
    }


    @GetMapping("/posts/new")
    ResponseEntity<List<PostData>> getNewPosts(@RequestAttribute(name="username", required = false) String requester,
                                               @RequestParam Integer limit,
                                               @RequestParam Integer offset){
        return new ResponseEntity<List<PostData>>(postService.getNewPosts(requester, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/posts/by-id/{post-id}")
    ResponseEntity<PostData> getPostById(@RequestAttribute(name="username", required = false) String requester,
                                         @PathVariable("post-id") Long postId){
        return new ResponseEntity<PostData>(postService.getPostById(requester, postId), HttpStatus.OK);
    }

    @PostMapping(value = "/posts/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PostData> uploadPost(@RequestAttribute String username,
                                          @RequestPart(name = "file", required = false) MultipartFile image,
                                          @RequestPart(name = "post") PostData post){
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