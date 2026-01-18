# Lobby Chat Implementation

This document describes the WebSocket-based chat implementation for the lobby using netty-socketio.

## Overview

The lobby chat feature allows players in a lobby to communicate in real-time using WebSocket connections. The implementation uses [netty-socketio](https://github.com/mrniko/netty-socketio) library for Socket.IO support.

## Architecture

### Components

1. **SocketIOConfig** - Configures the Socket.IO server
2. **SocketIOServerRunner** - Starts the Socket.IO server on application startup
3. **LobbyChatService** - Handles chat events and message broadcasting
4. **ChatMessageDto** - Data transfer object for chat messages
5. **ChatMessageRequestDto** - Request DTO for sending messages

### Configuration

The Socket.IO server is configured in `application.yml`:

```yaml
socketio:
  host: localhost
  port: 9092
```

You can override these values using environment variables or different profiles.

## Client Connection

### Authentication

Clients must authenticate using a JWT token when connecting:

```javascript
const socket = io('http://localhost:9092', {
  query: {
    token: 'your-jwt-token-here'
  }
});
```

The token is validated on connection, and the player's identity is extracted from the JWT claims.

## Events

### Client → Server Events

#### 1. `joinLobby`
Join a lobby's chat room.

**Payload:** `String` (lobbyId as UUID)

**Example:**
```javascript
socket.emit('joinLobby', 'lobby-uuid-here', (response) => {
  console.log(response); // "Joined lobby chat successfully"
});
```

**Validations:**
- Player must be authenticated
- Lobby must exist
- Player must be a member of the lobby

#### 2. `leaveLobby`
Leave the current lobby's chat room.

**Payload:** `String` (any value, not used)

**Example:**
```javascript
socket.emit('leaveLobby', '', (response) => {
  console.log(response); // "Left lobby chat successfully"
});
```

#### 3. `sendMessage`
Send a chat message to the lobby.

**Payload:** `ChatMessageRequestDto`
```json
{
  "message": "Hello everyone!"
}
```

**Example:**
```javascript
socket.emit('sendMessage', {
  message: 'Hello everyone!'
});
```

**Validations:**
- Player must be authenticated
- Player must be in a lobby
- Player must still be a member of the lobby
- Message cannot be empty
- Message cannot exceed 500 characters

### Server → Client Events

#### 1. `receiveMessage`
Receive a chat message from another player or the system.

**Payload:** `ChatMessageDto`
```json
{
  "playerId": "uuid-or-null-for-system",
  "playerName": "PlayerName",
  "avatarId": 1,
  "message": "Hello everyone!",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Example:**
```javascript
socket.on('receiveMessage', (message) => {
  console.log(`${message.playerName}: ${message.message}`);
});
```

**Note:** System messages have `playerId` and `avatarId` set to `null` and `playerName` set to "System".

#### 2. `error`
Receive error messages from the server.

**Payload:** `String` (error message)

**Example:**
```javascript
socket.on('error', (errorMessage) => {
  console.error('Chat error:', errorMessage);
});
```

## System Messages

The system automatically sends messages for the following events:

1. **Lobby Created:** "{playerName} created the lobby"
2. **Player Joined:** "{playerName} joined the lobby"
3. **Player Left:** "{playerName} left the lobby"

These messages are broadcast to all players in the lobby's chat room.

## Integration with Lobby Service

The `LobbyChatService` is integrated with `LobbyCommandService` to send system messages when:
- A lobby is created
- A player joins a lobby
- A player leaves a lobby

## Example Client Implementation

```javascript
// Connect to Socket.IO server
const socket = io('http://localhost:9092', {
  query: {
    token: localStorage.getItem('jwt-token')
  }
});

// Handle connection
socket.on('connect', () => {
  console.log('Connected to chat server');

  // Join lobby chat
  socket.emit('joinLobby', currentLobbyId, (response) => {
    console.log(response);
  });
});

// Handle incoming messages
socket.on('receiveMessage', (message) => {
  displayMessage(message);
});

// Handle errors
socket.on('error', (error) => {
  console.error('Chat error:', error);
});

// Send a message
function sendMessage(text) {
  socket.emit('sendMessage', {
    message: text
  });
}

// Leave lobby
function leaveLobby() {
  socket.emit('leaveLobby', '');
}

// Disconnect
socket.on('disconnect', () => {
  console.log('Disconnected from chat server');
});
```

## Security Considerations

1. **Authentication:** All connections require a valid JWT token
2. **Authorization:** Players can only join lobbies they are members of
3. **Validation:** Message content is validated (max 500 characters)
4. **Membership Verification:** Player membership is verified before sending messages

## Testing

To test the chat functionality:

1. Start the backend application
2. The Socket.IO server will start on port 9092 (or configured port)
3. Connect using a Socket.IO client with a valid JWT token
4. Join a lobby and send messages

## Troubleshooting

### Connection Issues
- Verify the Socket.IO server is running on the correct port
- Check that the JWT token is valid and not expired
- Ensure CORS is properly configured for your frontend origin

### Message Not Received
- Verify you've joined the lobby chat room using `joinLobby` event
- Check that you're still a member of the lobby
- Ensure the lobby still exists

### Authentication Errors
- Verify the JWT token is included in the connection query parameters
- Check that the token is valid and contains required claims (playerId, name, avatarId)
