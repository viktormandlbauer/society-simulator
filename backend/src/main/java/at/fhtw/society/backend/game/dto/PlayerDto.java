package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerDto {
    private UUID id;
    private String username;
}
