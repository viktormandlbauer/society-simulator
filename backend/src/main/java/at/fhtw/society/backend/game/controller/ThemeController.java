package at.fhtw.society.backend.game.controller;

import at.fhtw.society.backend.game.dto.AddThemeDto;
import at.fhtw.society.backend.game.service.ThemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<Object> getThemes() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", this.themeService.getAllThemes()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Unable to get themes: " + e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Object> addTheme(@RequestBody AddThemeDto addThemeDto) {
        try {

            this.themeService.addTheme(addThemeDto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", "Successfully added theme: " + addThemeDto.getThemeName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to add theme: " + e.getMessage()
            ));
        }
    }
}
