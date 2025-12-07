package at.fhtw.society.backend.game.repo;

import at.fhtw.society.backend.game.entity.Theme;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    List<Theme> findAll();
    Theme findByTheme(String name);
}
