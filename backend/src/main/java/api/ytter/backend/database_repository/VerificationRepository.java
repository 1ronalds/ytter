package api.ytter.backend.database_repository;

import api.ytter.backend.database_model.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// klase apraksta funkcijas, kas komunicē ar datubāzi. Šīs ir manuāli veidotās funkcijas un satur SQL vaicājumus.

@Repository
public interface VerificationRepository extends JpaRepository<VerificationEntity, Long> {
    Optional<VerificationEntity> findByVerificationKey(String verificationKey);
}