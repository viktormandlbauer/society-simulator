package at.fhtw.society.backend.game.service;

import at.fhtw.society.backend.game.dto.CreatePlayerDto;
import at.fhtw.society.backend.game.dto.PlayerDto;
import at.fhtw.society.backend.game.entity.Player;
import at.fhtw.society.backend.game.repo.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public UUID createPlayer(CreatePlayerDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        if (playerRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }

        Player player = new Player();
        player.setUsername(dto.getUsername());
        player.setPassword(dto.getPassword()); // TODO: hash this in real apps

        playerRepository.save(player);
        return player.getId();
    }

    public PlayerDto getPlayer(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return toDto(player);
    }

    public PlayerDto getByUsername(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + username));
        return toDto(player);
    }

    public List<PlayerDto> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deletePlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }
        playerRepository.deleteById(playerId);
    }

    private PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setId(player.getId());
        dto.setUsername(player.getUsername());
        return dto;
    }
}
