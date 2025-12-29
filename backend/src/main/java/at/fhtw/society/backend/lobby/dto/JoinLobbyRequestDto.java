package at.fhtw.society.backend.lobby.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinLobbyRequestDto {

    @Size(max = 64, message = "Password must not exceed 64 characters")
    private String password;
}
