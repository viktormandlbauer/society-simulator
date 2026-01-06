package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlayerDto {
    private String username;
    private String password;
}
