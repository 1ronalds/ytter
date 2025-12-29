package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// klase apraksta funkcijas, kas komunicē ar datubāzi. Šīs ir manuāli veidotās funkcijas un satur SQL vaicājumus.

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
}
