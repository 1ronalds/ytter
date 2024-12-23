package api.ytter.backend.controller;

import api.ytter.backend.model.FlagData;
import api.ytter.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportEndpoints {
    private final ReportService reportService;

    @PostMapping("/report/post/{post-id}")
    public ResponseEntity<Void> reportPost(@RequestAttribute String username,
                                           @PathVariable("post-id") Long postId){
        reportService.reportPost(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/report/comment/{comment-id}")
    public ResponseEntity<Void> reportComment(@RequestAttribute String username,
                                              @PathVariable("comment-id") Long commentId){
        reportService.reportComment(username, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<List<FlagData>> getReports(@RequestAttribute String username){
        return new ResponseEntity<>(reportService.getReports(username), HttpStatus.OK);
    }

    @DeleteMapping("/report/comment/{comment-id}")
    public ResponseEntity<Void> ignoreCommentReport(@RequestAttribute String username,
                                                    @PathVariable("comment-id") Long commentId) {
        reportService.ignoreReportComment(username, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/report/post/{post-id}")
    public ResponseEntity<Void> ignorePostReport(@RequestAttribute String username,
                                                 @PathVariable("post-id") Long postId) {
        reportService.ignoreReportPost(username, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}