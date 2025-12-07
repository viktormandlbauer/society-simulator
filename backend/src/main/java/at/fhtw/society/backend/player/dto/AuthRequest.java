package at.fhtw.society.backend.player.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "username is required")
        String username
) { }