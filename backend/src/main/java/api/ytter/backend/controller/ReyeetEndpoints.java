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

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

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

    @GetMapping("/reyeet-feed")
    ResponseEntity<List<ReyeetPostData>> getFollowingFeed(@RequestAttribute String username,
                                                          @RequestParam Integer limit,
                                                          @RequestParam Integer offset){
        return new ResponseEntity<List<ReyeetPostData>> (reyeetService.getFollowingReyeetFeed(username, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/reyeets/{username}")
    ResponseEntity<List<ReyeetPostData>> getReyeetsByUser(@RequestAttribute(name = "username", required = false) String requester,
                                                          @PathVariable String username,
                                                          @RequestParam Integer limit,
                                                          @RequestParam Integer offset){
        return new ResponseEntity<>(reyeetService.getReyeetsByUser(requester, username, limit, offset), HttpStatus.OK);
    }
}
