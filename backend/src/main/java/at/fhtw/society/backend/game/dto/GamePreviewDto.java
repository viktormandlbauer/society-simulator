package at.fhtw.society.backend.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class GamePreviewDto {
    private String gamemaster;
    private String theme;
}
