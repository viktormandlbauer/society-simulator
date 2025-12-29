package at.fhtw.society.backend.lobby.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateLobbyRequestDto {

    @NotBlank(message = "Lobby name must not be blank")
    @Size(max = 60, message = "Lobby name must not exceed 60 characters")
    private String name;

    @NotNull(message = "Theme ID must not be null")
    private UUID themeId;

    @Min(value = 2, message = "Maximum players must be at least 2")
    @Max(value = 8, message = "Maximum players must not exceed 8")
    private int maxPlayers;

    @Min(value = 3, message = "Maximum rounds must be at least 3")
    @Max(value = 20, message = "Maximum rounds must not exceed 20")
    private int maxRounds;

    @Size(max = 64, message = "Password must not exceed 64 characters")
    private String password;
}
