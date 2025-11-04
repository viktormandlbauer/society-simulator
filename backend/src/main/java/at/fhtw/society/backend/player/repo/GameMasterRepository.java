package at.fhtw.society.backend.player.repo;

import at.fhtw.society.backend.player.entity.GameMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameMasterRepository extends JpaRepository<GameMaster, Long> { }