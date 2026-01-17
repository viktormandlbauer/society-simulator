package at.fhtw.society.backend.game.controller;

import at.fhtw.society.backend.game.dto.DilemmaDto;
import at.fhtw.society.backend.game.dto.FinalOutcomeDto;
import at.fhtw.society.backend.game.dto.VoteRequestDto;
import at.fhtw.society.backend.game.dto.VoteResultDto;
import at.fhtw.society.backend.game.service.GameService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateGameRequest req) {
        UUID id = gameService.createGame(req.getLobbyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "data", Map.of("gameId", id)));
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<Object> join(@PathVariable UUID gameId, @RequestBody JoinRequest req) {
        gameService.joinGame(gameId, req.getPlayerId());
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("gameId", gameId, "playerId", req.getPlayerId())));
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<Object> start(@PathVariable UUID gameId) {
        gameService.startGame(gameId);
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("gameId", gameId)));
    }

    @GetMapping("/{gameId}/intro")
    public ResponseEntity<Object> intro(@PathVariable UUID gameId) {
        return ResponseEntity.ok(Map.of("status", "success", "data", gameService.getIntro(gameId)));
    }

    /** current dilemma (no AI call) */
    @GetMapping("/{gameId}/dilemma")
    public ResponseEntity<Object> currentDilemma(@PathVariable UUID gameId) {
        DilemmaDto dto = gameService.getCurrentDilemma(gameId);
        return ResponseEntity.ok(Map.of("status", "success", "data", dto));
    }

    /** optional: manual next round trigger */
    @PostMapping("/{gameId}/rounds/new")
    public ResponseEntity<Object> newRound(@PathVariable UUID gameId) {
        DilemmaDto dto = gameService.newRound(gameId);
        return ResponseEntity.ok(Map.of("status", "success", "data", dto));
    }

    /** vote */
    @PostMapping("/{gameId}/dilemma/vote")
    public ResponseEntity<Object> vote(@PathVariable UUID gameId, @RequestBody VoteRequestDto req) {
        VoteResultDto res = gameService.vote(gameId, req);
        return ResponseEntity.ok(Map.of("status", "success", "data", res));
    }

    /** Get final outcome for a completed game */
    @GetMapping("/{gameId}/outcome")
    public ResponseEntity<Object> getFinalOutcome(@PathVariable UUID gameId) {
        FinalOutcomeDto outcome = gameService.getFinalOutcome(gameId);
        return ResponseEntity.ok(Map.of("status", "success", "data", outcome));
    }

    @Getter @Setter
    public static class CreateGameRequest {
        private UUID lobbyId;
    }

    @Getter @Setter
    public static class JoinRequest {
        private UUID playerId;
    }
}
