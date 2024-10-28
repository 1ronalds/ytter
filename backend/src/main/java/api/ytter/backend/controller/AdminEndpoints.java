package api.ytter.backend.controller;

import api.ytter.backend.service.CommentService;
import api.ytter.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminEndpoints {
    private final PostService postService;
    private final CommentService commentService;

    @DeleteMapping("/admin/post/{post-id}")
    public ResponseEntity<Void> deletePost(@RequestAttribute String username, @PathVariable("post-id") Long postId){
        postService.deletePost(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/admin/comment/{comment-id}")
    public ResponseEntity<Void> deleteComment(@RequestAttribute String username, @PathVariable("post-id") Long commentId){
        commentService.deleteComment(username, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
