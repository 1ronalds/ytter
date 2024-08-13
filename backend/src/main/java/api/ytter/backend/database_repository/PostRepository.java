package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.PostEntity;
import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p FROM Post p WHERE p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.likes DESC")
    List<PostEntity> findAllByDateRangeSortedByLikes(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);

    Optional<PostEntity> findByImageId(Long imageId);


    Page<PostEntity> findAllByOrderByIdDesc(Pageable pageable);

    List<PostEntity> findByUser(UserEntity user, Pageable pageable);
}
