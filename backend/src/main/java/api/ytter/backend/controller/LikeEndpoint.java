package api.ytter.backend.controller;

import api.ytter.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

@RequiredArgsConstructor
@RestController
public class LikeEndpoint {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Void> likePost(@RequestAttribute String username, @PathVariable Long postId){
        likeService.likePostAdd(username, postId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> likeComment(@RequestAttribute String username, @PathVariable Long commentId) {
        likeService.likeCommentAdd(username, commentId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/post/{postId}/like")
    public ResponseEntity<Void> unlikePost(@RequestAttribute String username, @PathVariable Long postId){
        likeService.likePostRemove(username, postId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@RequestAttribute String username, @PathVariable Long commentId) {
        likeService.likeCommentRemove(username, commentId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
