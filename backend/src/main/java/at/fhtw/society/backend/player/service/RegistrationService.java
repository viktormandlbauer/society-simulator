// java
package at.fhtw.society.backend.player.service;

import at.fhtw.society.backend.player.repo.GameMasterRepository;
import at.fhtw.society.backend.player.repo.PlayerRepository;
import at.fhtw.society.backend.player.repo.UserRepository;
import at.fhtw.society.backend.player.entity.GameMaster;
import at.fhtw.society.backend.player.entity.Player;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepo;
    private final PlayerRepository playerRepo;
    private final GameMasterRepository gmRepo;

    public RegistrationService(UserRepository userRepo, PlayerRepository playerRepo, GameMasterRepository gmRepo) {
        this.userRepo = userRepo;
        this.playerRepo = playerRepo;
        this.gmRepo = gmRepo;
    }

    @Transactional
    public long registerPlayer(String username) {
        ensureUsernameIsFree(username);
        Player p = new Player();
        p.setUsername(username);
        Player saved = playerRepo.save(p);
        return saved.getId();
    }

    @Transactional
    public long registerGamemaster(String username) {
        ensureUsernameIsFree(username);
        GameMaster gm = new GameMaster();
        gm.setUsername(username);
        GameMaster saved = gmRepo.save(gm);
        return saved.getId();
    }

    private void ensureUsernameIsFree(String username) {
        if (userRepo.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("username already taken: " + username);
        }
    }
}