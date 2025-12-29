package api.ytter.backend.controller;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.model.CommentData;
import api.ytter.backend.model.PostData;
import api.ytter.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

@RequiredArgsConstructor
@RestController
public class CommentEndpoints {

    private final CommentService commentService;

    @GetMapping("/comment/to-post/{postId}")
    public ResponseEntity<List<CommentData>> getCommentsToPost(@RequestAttribute(required = false) String username, @PathVariable Long postId){
        return new ResponseEntity<List<CommentData>>(commentService.getCommentsToPost(username, postId), HttpStatus.OK);
    }

    @GetMapping("/comment/to-comment/{commentId}")
    public ResponseEntity<List<CommentData>> getCommentsToComment(@RequestAttribute(required = false) String username, @PathVariable Long commentId){
        return new ResponseEntity<List<CommentData>>(commentService.getCommentsToComment(username, commentId), HttpStatus.OK);
    }

    @PostMapping("/comment/to-post/create")
    public ResponseEntity<CommentData> postComment(@RequestAttribute String username, @RequestBody CommentData commentData){
        return new ResponseEntity<CommentData>(commentService.createComment(commentData, username), HttpStatus.OK);
    }

    @PostMapping("/comment/to-comment/create")
    public ResponseEntity<CommentData> postCommentToComment(@RequestAttribute String username, @RequestBody CommentData commentData){
        return new ResponseEntity<CommentData> (commentService.createComment(commentData, username), HttpStatus.OK);
    }

    @DeleteMapping("/comment/delete/{comment-id}")
    public ResponseEntity<Void> deleteComment(@RequestAttribute String username, @PathVariable("comment-id") Long commentId){
        commentService.deleteComment(username, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
