package api.ytter.backend.service;

import api.ytter.backend.database_model.CommentEntity;
import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_repository.CommentRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.FlagData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void reportPost(String username, Long postId) {
        if (userRepository.findByUsername(username).isPresent()) {
            PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new InvalidDataException("Post doesnt exist"));
            postEntity.setReported(true);
            postRepository.save(postEntity);
        }
    }

    public void reportComment(String username, Long commentId) {
        if (userRepository.findByUsername(username).isPresent()) {
            CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new InvalidDataException("Comment doesnt exist"));
            commentEntity.setReported(true);
            commentRepository.save(commentEntity);
        }
    }

    public List<FlagData> getReports(String username) {
        if (userRepository.findByUsername(username).get().getIsAdmin()) {
            List<PostEntity> reportedPosts = postRepository.findAllByReported();
            List<CommentEntity> reportedComments = commentRepository.findAllByReported();
            List<Object> reported = new ArrayList<>();
            reported.addAll(reportedPosts);
            reported.addAll(reportedComments);
            reported.sort((o1, o2) -> {
                Date timestamp1 = (o1 instanceof PostEntity) ? ((PostEntity) o1).getTimestamp() : ((CommentEntity) o1).getTimestamp();
                Date timestamp2 = (o2 instanceof PostEntity) ? ((PostEntity) o2).getTimestamp() : ((CommentEntity) o2).getTimestamp();
                return timestamp2.compareTo(timestamp1);
            });
            return reported.stream().map(o -> {
                String message = (o instanceof PostEntity) ? ((PostEntity) o).getText() : ((CommentEntity) o).getComment();
                Long postId = (o instanceof PostEntity) ? ((PostEntity) o).getId() : null;
                Long commentId = (o instanceof CommentEntity) ? ((CommentEntity) o).getId() : null;
                return new FlagData(message, postId != null ? postId.toString() : "", commentId != null ? commentId.toString() : "");
            }).toList();
        } else {
            throw new InvalidDataException("Not admin");
        }
    }

    public void ignoreReportPost(String username, Long postId) {
        if (userRepository.findByUsername(username).get().getIsAdmin()) {
            PostEntity postEntity = postRepository.findById(postId).orElseThrow(()->new InvalidDataException("Post doesnt exist"));
            postEntity.setReported(false);
            postRepository.save(postEntity);

        }
    }

    public void ignoreReportComment(String username, Long commentId) {
        if (userRepository.findByUsername(username).get().getIsAdmin()) {
            CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(()->new InvalidDataException("Comment doesnt exist"));
            commentEntity.setReported(false);
            commentRepository.save(commentEntity);
        }
    }
}
