package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Theme;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ThemeRepository extends JpaRepository<Theme, UUID> {
    List<Theme> findAll();
    Theme findByTheme(String name);
}
