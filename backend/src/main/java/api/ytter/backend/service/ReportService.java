package api.ytter.backend.service;

import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.model.FlagData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Void reportPost(String username, Long postId){
        if()
    }

    public Void reportComment(String username, Long commentId){
        return null;
    }

    public List<FlagData> getReports(String username, Integer limit, Integer offset){
        return null;
    }
}
