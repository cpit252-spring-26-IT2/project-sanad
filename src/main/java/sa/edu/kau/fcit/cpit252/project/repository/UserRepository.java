package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
