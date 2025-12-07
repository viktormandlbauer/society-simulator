package at.fhtw.society.backend.game.service;

import at.fhtw.society.backend.game.dto.AddThemeDto;
import at.fhtw.society.backend.game.entity.Theme;
import at.fhtw.society.backend.game.repo.GameRepository;
import at.fhtw.society.backend.game.repo.ThemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {

    private static final Logger log = LoggerFactory.getLogger(ThemeService.class);

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<Theme> getAllThemes() {
        return this.themeRepository.findAll();
    }

    public void addTheme(AddThemeDto addThemeDto) {
        Theme theme = new Theme(addThemeDto);
        themeRepository.save(theme);
    }
}
