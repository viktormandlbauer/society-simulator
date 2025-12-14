package at.fhtw.society.backend.lobby.repo;

import at.fhtw.society.backend.lobby.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LobbyRepository extends JpaRepository<Lobby, UUID> {
}
