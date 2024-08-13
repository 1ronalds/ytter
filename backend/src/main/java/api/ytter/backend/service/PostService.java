package api.ytter.backend.service;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_repository.LikeRepository;
import api.ytter.backend.database_repository.PostRepository;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.model.PostData;
import api.ytter.backend.other.FileObject;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PostService {
    private static final String UPLOAD_DIR = "uploads/";
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/bmp", "image/webp");
    private static final Map<String, String> magicNumbers = new HashMap<>();
    private static final Map<String, String> mimeExtension = new HashMap<>();
    private final Tika tika = new Tika();
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    static {
        magicNumbers.put("FFD8FF", "jpg");
        magicNumbers.put("89504E47", "png");
        magicNumbers.put("424D", "bmp");
        magicNumbers.put("52494646", "webp");

        mimeExtension.put("jpg", "image/jpeg");
        mimeExtension.put("png", "image/png");
        mimeExtension.put("bmp", "image/bmp");
        mimeExtension.put("webp", "image/webp");
    }


    public List<PostData> getPostsByUsername(String username, Integer limit, Integer offset ){
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("timestamp").descending());
        return postRepository.findByUser(userRepository.findByUsername(username).orElseThrow(RuntimeException::new), pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        postEntity.getUser().getId(),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        true //change later
                        ))
                .toList();

    }

    public List<PostData> getTopPostsPast7Days(Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(7);

        return postRepository.findAllByDateRangeSortedByLikes(startDate, now, pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        postEntity.getUser().getId(),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        true //change later
                )).toList();
    }

    public List<PostData> getTopPostsPast30Days(Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(7);

        return postRepository.findAllByDateRangeSortedByLikes(startDate, now, pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        postEntity.getUser().getId(),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        true //change later
                )).toList();
    }

    public List<PostData> getNewPosts(Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit);
        return postRepository.findAllByOrderByIdDesc(pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        postEntity.getUser().getId(),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        true //change later
                )).toList();
    }

    public PostData getPostById(Long PostId){
        PostData postData = new PostData();
        PostEntity postEntity = postRepository.findById(PostId).orElseThrow(RuntimeException::new);
        postData.setPostId(postEntity.getId());
        postData.setUserId(postEntity.getUser().getId());
        postData.setImageId(postEntity.getImageId());
        postData.setTimestamp(postEntity.getTimestamp());
        postData.setText(postEntity.getText());
        return postData;
    }

    private String getFileExtension(byte[] byteStream) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteStream) {
            String hex = Integer.toHexString(0xFF & b).toUpperCase();
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        String fileSignature = hexString.toString();
        for (Map.Entry<String, String> entry : magicNumbers.entrySet()) {
            if (fileSignature.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new RuntimeException();
    }


    public FileObject getImage(Long imageId){
        FileObject fileObject = new FileObject();
        Path path = Paths.get(UPLOAD_DIR + imageId);
        if(Files.exists(path)){
            try {
                byte[] file = Files.readAllBytes(path);
                String fileExtension = getFileExtension(file);
                fileObject.setFile(file);
                fileObject.setFilename(imageId + fileExtension);
                fileObject.setMimeType(mimeExtension.get(fileExtension));
            } catch(Exception ex){
                throw new RuntimeException();
            }

        } else {
            throw new RuntimeException();
        }
        return fileObject;
    }

    public PostEntity uploadPost(MultipartFile image, PostData post, String username) {
        Long imageId = null;
        if(!image.isEmpty()){
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (!ALLOWED_MIME_TYPES.contains(tika.detect(image.getInputStream()))){
                    throw new RuntimeException();
                }

                byte[] imageBytes = image.getBytes();
                Random random = new Random();
                do {
                    imageId = random.nextLong();
                } while (postRepository.findByImageId(imageId).isPresent());
                String name = Long.toString(imageId);
                Path path = uploadPath.resolve(name);
                Files.write(path, imageBytes);
            } catch(IOException e){
                throw new RuntimeException();
            }
        }
        Long postId;
        Random random = new Random();
        do {
            postId = random.nextLong();
        } while (postRepository.existsById(postId));

        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);
        postEntity.setImageId(imageId);
        postEntity.setUser(userRepository.findByUsername(username).orElseThrow(RuntimeException::new));
        postEntity.setText(post.getText());
        if(imageId != null) postEntity.setImageId(imageId);
        postEntity.setTimestamp(new Date());
        postRepository.save(postEntity);
        return postEntity;
    }
}
