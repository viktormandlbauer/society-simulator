package at.fhtw.society.backend.lobby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}
