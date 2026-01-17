package at.fhtw.society.backend.player;

import at.fhtw.society.backend.player.controller.AuthController;
import at.fhtw.society.backend.player.dto.AuthRequest;
import at.fhtw.society.backend.player.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(RegistrationControllerTest.TestConfig.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService registrationService() {
            return Mockito.mock(AuthService.class);
        }
    }

    @Test
    @DisplayName("POST /api/players/player/register - success")
    void registerPlayerSuccess() throws Exception {
        doReturn(42L).when(registrationService).registerPlayer("alice");

        var req = new AuthRequest("alice");
        mockMvc.perform(post("/api/players/player/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/players/player/42"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(42))
                .andExpect(jsonPath("$.data.role").value("PLAYER"));
    }

    @Test
    @DisplayName("POST /api/players/gamemaster/register - success")
    void registerGamemasterSuccess() throws Exception {
        doReturn(7L).when(registrationService).registerGamemaster("gm");

        var req = new AuthRequest("gm");
        mockMvc.perform(post("/api/players/gamemaster/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/players/gamemaster/7"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.role").value("GAMEMASTER"));
    }

    @Test
    @DisplayName("POST /api/players/player/register - conflict when username taken")
    void registerPlayerConflict() throws Exception {
        doThrow(new IllegalArgumentException("username already taken: alice"))
                .when(registrationService).registerPlayer(anyString());

        var req = new AuthRequest("alice");
        mockMvc.perform(post("/api/players/player/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("username already taken: alice"));
    }

    @Test
    @DisplayName("POST /api/players/player/register - validation failure for blank username")
    void registerPlayerValidationFailure() throws Exception {
        var payload = "{ \"username\": \"\" }";
        mockMvc.perform(post("/api/players/player/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}