package at.fhtw.society.backend.player.repo;

import at.fhtw.society.backend.player.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameIgnoreCase(String username);
}