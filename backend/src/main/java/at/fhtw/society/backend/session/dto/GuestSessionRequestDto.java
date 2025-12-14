package at.fhtw.society.backend.session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GuestSessionRequestDto {
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "AvatarId must not be null")
    private AvatarId avatarId;
}
