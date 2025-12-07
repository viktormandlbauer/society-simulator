// java
package at.fhtw.society.backend.player.service;

import at.fhtw.society.backend.player.repo.GamemasterRepository;
import at.fhtw.society.backend.player.repo.PlayerRepository;
import at.fhtw.society.backend.player.repo.UserRepository;
import at.fhtw.society.backend.player.entity.Gamemaster;
import at.fhtw.society.backend.player.entity.Player;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepo;
    private final PlayerRepository playerRepo;
    private final GamemasterRepository gmRepo;

    public RegistrationService(UserRepository userRepo, PlayerRepository playerRepo, GamemasterRepository gmRepo) {
        this.userRepo = userRepo;
        this.playerRepo = playerRepo;
        this.gmRepo = gmRepo;
    }

    @Transactional
    public UUID registerPlayer(String username) {
        ensureUsernameIsFree(username);
        Player p = new Player();
        p.setUsername(username);
        Player saved = playerRepo.save(p);
        return saved.getId();
    }

    @Transactional
    public UUID registerGamemaster(String username) {
        ensureUsernameIsFree(username);
        Gamemaster gm = new Gamemaster();
        gm.setUsername(username);
        Gamemaster saved = gmRepo.save(gm);
        return saved.getId();
    }

    private void ensureUsernameIsFree(String username) {
        if (userRepo.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("username already taken: " + username);
        }
    }
}