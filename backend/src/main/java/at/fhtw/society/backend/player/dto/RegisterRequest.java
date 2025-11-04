package at.fhtw.society.backend.player.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        String username
) { }