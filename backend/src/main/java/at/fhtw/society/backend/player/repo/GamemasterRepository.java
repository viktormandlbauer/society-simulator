package at.fhtw.society.backend.player.repo;

import at.fhtw.society.backend.player.entity.Gamemaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GamemasterRepository extends JpaRepository<Gamemaster, Long> {
    Gamemaster findById(UUID gamemasterId);
    Gamemaster findByUsername(String username);
}