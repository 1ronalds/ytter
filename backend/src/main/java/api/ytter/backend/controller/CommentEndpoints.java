package api.ytter.backend.controller;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.model.CommentData;
import api.ytter.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentEndpoints {

    private final CommentService commentService;

    @GetMapping("/comment/to-post/{postId}")
    public ResponseEntity<List<CommentData>> getCommentsToPost(@PathVariable Long postId){
        return new ResponseEntity<List<CommentData>>(commentService.getCommentsToPost(postId), HttpStatus.OK);
    }

    @GetMapping("/comment/to-comment/{commentId}")
    public ResponseEntity<List<CommentData>> getCommentsToComment(@PathVariable Long commentId){
        return new ResponseEntity<List<CommentData>>(commentService.getCommentsToComment(commentId), HttpStatus.OK);
    }

    @PostMapping("/comment/to-post/create")
    public ResponseEntity<CommentData> postComment(@RequestBody CommentData commentData){
        return new ResponseEntity<CommentData>(commentService.createComment(commentData), HttpStatus.OK);
    }

    @PostMapping("/comment/to-comment/create")
    public ResponseEntity<CommentData> postCommentToComment(@RequestBody CommentData commentData){
        return new ResponseEntity<CommentData> (commentService.createComment(commentData), HttpStatus.OK);
    }

}
