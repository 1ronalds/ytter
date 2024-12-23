package api.ytter.backend.service;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.*;
import api.ytter.backend.exception.exception_types.InvalidDataException;
import api.ytter.backend.model.PostData;
import api.ytter.backend.model.ProfilePublicData;
import api.ytter.backend.other.FileObject;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
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
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/bmp", "image/webp");
    private static final Map<String, String> magicNumbers = new HashMap<>();
    private static final Map<String, String> mimeExtension = new HashMap<>();
    private final Tika tika = new Tika();
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ReyeetRepository reyeetRepository;
    private final CommentRepository commentRepository;
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


    @Value("${ytter.uploads}")
    private String UPLOAD_DIR;

    public List<PostData> getFollowingFeed(String username, Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "timestamp_"));
        List<PostEntity> posts = postRepository.
                findAllPostsByUsersFollowing(userRepository.findByUsername(username).orElseThrow(RuntimeException::new).getId(), pageable);
        return posts.stream()
                .map(postEntity -> new PostData(postEntity.getId(),
                new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()),
                postEntity.getImageId(),
                postEntity.getReplyCount(),
                postEntity.getLikeCount(),
                postEntity.getReyeetCount(),
                postEntity.getText(),
                postEntity.getTimestamp(),
                likeRepository.findByUserAndPost(userRepository.findByUsername(username).orElseThrow(RuntimeException::new), postEntity).isPresent(),
                reyeetRepository.findByUserAndPost(userRepository.findByUsername(username).orElseThrow(RuntimeException::new), postEntity).isPresent()
        )).toList();

    }

    public List<PostData> getPostsByUsername(String username, Integer limit, Integer offset ){
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "timestamp_"));
        return postRepository.findByUser(userRepository.findByUsername(username).orElseThrow(RuntimeException::new), pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        reyeetRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent()
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
                        new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        reyeetRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent()
                )).toList();
    }

    public List<PostData> getTopPostsPast30Days(Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(30);

        return postRepository.findAllByDateRangeSortedByLikes(startDate, now, pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        reyeetRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent()
                )).toList();
    }

    public List<PostData> getNewPosts(Integer limit, Integer offset){
        Pageable pageable = PageRequest.of(offset, limit);
        return postRepository.findAllByOrderByTimestamp_Desc(pageable)
                .stream()
                .map(postEntity -> new PostData(
                        postEntity.getId(),
                        new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()),
                        postEntity.getImageId(),
                        postEntity.getReplyCount(),
                        postEntity.getLikeCount(),
                        postEntity.getReyeetCount(),
                        postEntity.getText(),
                        postEntity.getTimestamp(),
                        likeRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent(),
                        reyeetRepository.findByUserAndPost(postEntity.getUser(), postEntity).isPresent()
                )).toList();
    }

    public PostData getPostById(Long PostId){
        PostData postData = new PostData();
        PostEntity postEntity = postRepository.findById(PostId).orElseThrow(RuntimeException::new);
        postData.setPostId(postEntity.getId());
        postData.setProfileDataPublic(new ProfilePublicData(postEntity.getUser().getUsername(), postEntity.getUser().getName()));
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
        Path path = Paths.get(UPLOAD_DIR, String.valueOf(imageId));
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
            throw new InvalidDataException("File with this id doesnt exist");
        }
        return fileObject;
    }

    public PostData uploadPost(MultipartFile image, PostData post, String username) {
        Long imageId = null;
        if(image != null){
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (!ALLOWED_MIME_TYPES.contains(tika.detect(image.getInputStream()))){
                    throw new InvalidDataException("Not supported file format");
                }

                byte[] imageBytes = image.getBytes();
                Random random = new Random();
                do {
                    imageId = Math.abs(random.nextLong());
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
            postId = Math.abs(random.nextLong());
        } while (postRepository.existsById(postId));

        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);
        postEntity.setImageId(imageId);
        postEntity.setReplyCount(0L);
        postEntity.setLikeCount(0L);
        postEntity.setReyeetCount(0L);
        postEntity.setReported(false);
        postEntity.setUser(userRepository.findByUsername(username).orElseThrow(RuntimeException::new));
        postEntity.setText(post.getText());
        if(imageId != null) postEntity.setImageId(imageId);
        postEntity.setTimestamp(new Date());
        PostEntity saved = postRepository.save(postEntity);
        return getPostById(saved.getId());
    }

    public void deletePost(String username, Long postId){
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        if(postRepository.findById(postId).get().getUser().equals(userEntity) || userEntity.getIsAdmin()){
            commentRepository.deleteAllByRootPostId(postId);
            postRepository.deleteById(postId);

        } else {
            throw new InvalidDataException("Not your post, not admin");
        }
    }
}
