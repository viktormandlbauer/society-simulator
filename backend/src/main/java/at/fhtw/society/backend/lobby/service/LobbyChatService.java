package at.fhtw.society.backend.lobby.service;

import at.fhtw.society.backend.lobby.dto.ChatMessageDto;
import at.fhtw.society.backend.lobby.dto.ChatMessageRequestDto;
import at.fhtw.society.backend.lobby.entity.LobbyMember;
import at.fhtw.society.backend.lobby.exception.LobbyMemberNotFoundException;
import at.fhtw.society.backend.lobby.exception.LobbyNotFoundException;
import at.fhtw.society.backend.lobby.repo.LobbyMemberRepository;
import at.fhtw.society.backend.lobby.repo.LobbyRepository;
import at.fhtw.society.backend.security.jwt.JwtService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LobbyChatService {

    private static final String EVENT_JOIN_LOBBY = "joinLobby";
    private static final String EVENT_LEAVE_LOBBY = "leaveLobby";
    private static final String EVENT_SEND_MESSAGE = "sendMessage";
    private static final String EVENT_RECEIVE_MESSAGE = "receiveMessage";
    private static final String EVENT_ERROR = "error";

    private final SocketIOServer server;
    private final JwtService jwtService;
    private final LobbyRepository lobbyRepository;
    private final LobbyMemberRepository lobbyMemberRepository;

    // Map to track which lobby each client is in
    private final ConcurrentHashMap<UUID, UUID> clientToLobbyMap = new ConcurrentHashMap<>();

    public LobbyChatService(SocketIOServer server,
                           JwtService jwtService,
                           LobbyRepository lobbyRepository,
                           LobbyMemberRepository lobbyMemberRepository) {
        this.server = server;
        this.jwtService = jwtService;
        this.lobbyRepository = lobbyRepository;
        this.lobbyMemberRepository = lobbyMemberRepository;
    }

    @PostConstruct
    public void init() {
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener(EVENT_JOIN_LOBBY, String.class, onJoinLobby());
        server.addEventListener(EVENT_LEAVE_LOBBY, String.class, onLeaveLobby());
        server.addEventListener(EVENT_SEND_MESSAGE, ChatMessageRequestDto.class, onSendMessage());
        log.info("LobbyChatService initialized with event listeners");
    }

    @PreDestroy
    public void destroy() {
        server.stop();
        log.info("SocketIO server stopped");
    }

    private ConnectListener onConnected() {
        return client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (token == null || token.isBlank()) {
                log.warn("Client {} connected without token", client.getSessionId());
                client.sendEvent(EVENT_ERROR, "Authentication required");
                client.disconnect();
                return;
            }

            try {
                JwtService.PlayerIdentity identity = jwtService.toPlayerIdentity(jwtService.decodeAndValidate(token));
                client.set("playerId", identity.playerId());
                client.set("playerName", identity.name());
                client.set("avatarId", identity.avatarId().getId());
                log.info("Client {} connected as player {} ({})", client.getSessionId(), identity.name(), identity.playerId());
            } catch (JwtException e) {
                log.warn("Client {} connected with invalid token: {}", client.getSessionId(), e.getMessage());
                client.sendEvent(EVENT_ERROR, "Invalid authentication token");
                client.disconnect();
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            UUID playerId = client.get("playerId");
            if (playerId != null) {
                UUID lobbyId = clientToLobbyMap.remove(playerId);
                if (lobbyId != null) {
                    client.leaveRoom(lobbyId.toString());
                    log.info("Player {} disconnected from lobby {}", playerId, lobbyId);
                }
            }
            log.info("Client {} disconnected", client.getSessionId());
        };
    }

    private DataListener<String> onJoinLobby() {
        return (client, lobbyIdStr, ackSender) -> {
            UUID playerId = client.get("playerId");
            if (playerId == null) {
                client.sendEvent(EVENT_ERROR, "Not authenticated");
                return;
            }

            try {
                UUID lobbyId = UUID.fromString(lobbyIdStr);

                // Verify lobby exists
                if (!lobbyRepository.existsById(lobbyId)) {
                    client.sendEvent(EVENT_ERROR, "Lobby not found");
                    return;
                }

                // Verify player is a member of the lobby
                LobbyMember member = lobbyMemberRepository.findByLobby_IdAndPlayerId(lobbyId, playerId)
                        .orElse(null);

                if (member == null) {
                    client.sendEvent(EVENT_ERROR, "You are not a member of this lobby");
                    return;
                }

                // Leave previous lobby if any
                UUID previousLobbyId = clientToLobbyMap.get(playerId);
                if (previousLobbyId != null) {
                    client.leaveRoom(previousLobbyId.toString());
                }

                // Join the new lobby room
                client.joinRoom(lobbyId.toString());
                clientToLobbyMap.put(playerId, lobbyId);

                log.info("Player {} joined lobby {} chat", playerId, lobbyId);

                // Send acknowledgment
                ackSender.sendAckData("Joined lobby chat successfully");

            } catch (IllegalArgumentException e) {
                client.sendEvent(EVENT_ERROR, "Invalid lobby ID");
            }
        };
    }

    private DataListener<String> onLeaveLobby() {
        return (client, data, ackSender) -> {
            UUID playerId = client.get("playerId");
            if (playerId == null) {
                client.sendEvent(EVENT_ERROR, "Not authenticated");
                return;
            }

            UUID lobbyId = clientToLobbyMap.remove(playerId);
            if (lobbyId != null) {
                client.leaveRoom(lobbyId.toString());
                log.info("Player {} left lobby {} chat", playerId, lobbyId);

                ackSender.sendAckData("Left lobby chat successfully");
            }
        };
    }

    private DataListener<ChatMessageRequestDto> onSendMessage() {
        return (client, messageRequest, ackSender) -> {
            UUID playerId = client.get("playerId");
            String playerName = client.get("playerName");
            String avatarId = client.get("avatarId");

            if (playerId == null) {
                client.sendEvent(EVENT_ERROR, "Not authenticated");
                return;
            }

            UUID lobbyId = clientToLobbyMap.get(playerId);
            if (lobbyId == null) {
                client.sendEvent(EVENT_ERROR, "You are not in a lobby");
                return;
            }

            // Verify player is still a member of the lobby
            if (!lobbyMemberRepository.existsByLobby_IdAndPlayerId(lobbyId, playerId)) {
                client.sendEvent(EVENT_ERROR, "You are no longer a member of this lobby");
                clientToLobbyMap.remove(playerId);
                client.leaveRoom(lobbyId.toString());
                return;
            }

            // Create chat message
            ChatMessageDto chatMessage = ChatMessageDto.builder()
                    .playerId(playerId)
                    .playerName(playerName)
                    .avatarId(avatarId)
                    .message(messageRequest.getMessage())
                    .timestamp(Instant.now())
                    .build();

            // Broadcast message to all clients in the lobby room
            server.getRoomOperations(lobbyId.toString()).sendEvent(EVENT_RECEIVE_MESSAGE, chatMessage);

            log.info("Player {} sent message in lobby {}: {}", playerId, lobbyId, messageRequest.getMessage());
        };
    }

    /**
     * Sends a system message to all members of a lobby.
     * This can be used for notifications like "Player X joined the lobby"
     */
    public void sendSystemMessage(UUID lobbyId, String message) {
        ChatMessageDto systemMessage = ChatMessageDto.builder()
                .playerId(null)
                .playerName("System")
                .avatarId(null)
                .message(message)
                .timestamp(Instant.now())
                .build();

        server.getRoomOperations(lobbyId.toString()).sendEvent(EVENT_RECEIVE_MESSAGE, systemMessage);
        log.info("System message sent to lobby {}: {}", lobbyId, message);
    }
}
