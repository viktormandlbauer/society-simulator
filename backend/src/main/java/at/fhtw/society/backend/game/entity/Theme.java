package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.game.dto.AddThemeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "theme", nullable = false, length = 256)
    private String theme;

    public Theme(String theme) {
        this.theme = theme;
    }

    public Theme(AddThemeDto addThemeDto) {
        this.theme = addThemeDto.getThemeName();
    }
}
