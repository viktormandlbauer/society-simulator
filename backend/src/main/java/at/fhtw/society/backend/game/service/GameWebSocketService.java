package at.fhtw.society.backend.game.service;

import at.fhtw.society.backend.game.dto.VoteResultDto;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class GameWebSocketService {

    private static final String EVENT_VOTE_COMPLETED = "voteCompleted";

    private final SocketIOServer server;

    public GameWebSocketService(SocketIOServer server) {
        this.server = server;
    }

    /**
     * Notifies all players in a game that voting has completed for the current round.
     * Sends the vote result including outcome summary and next dilemma.
     *
     * @param gameId - ID of the game
     * @param voteResult - The voting result with outcome and next dilemma
     */
    public void notifyVoteCompleted(UUID gameId, VoteResultDto voteResult) {
        // Use game ID as the room name for broadcasting
        String roomName = "game:" + gameId.toString();
        server.getRoomOperations(roomName).sendEvent(EVENT_VOTE_COMPLETED, voteResult);
        log.info("Vote completed notification sent to game {}: round={}, completed={}",
                gameId, voteResult.getRoundNumber(), voteResult.isRoundCompleted());
    }

    /**
     * Adds a client to a game room so they can receive game events.
     * This should be called when a player joins/connects to a game.
     *
     * @param gameId - ID of the game
     * @param sessionId - Socket.IO session ID of the client
     */
    public void joinGameRoom(UUID gameId, UUID sessionId) {
        String roomName = "game:" + gameId.toString();
        // Note: This would require access to the client, which we'll handle in LobbyChatService
        log.info("Player session {} should join game room {}", sessionId, roomName);
    }
}
