package at.fhtw.society.backend.player.repo;

import at.fhtw.society.backend.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> { }