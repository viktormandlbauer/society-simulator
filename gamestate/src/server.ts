import http from "http";
import { Server } from "socket.io";
import { createApp } from "./app";

type ConnectedUser = {
  id: string;
  name: string;
  avatarClass?: string;
};

const toCleanString = (value: unknown): string | null => {
  if (Array.isArray(value)) {
    for (const item of value) {
      const cleaned = toCleanString(item);
      if (cleaned) return cleaned;
    }
    return null;
  }

  if (typeof value === "number") {
    return value.toString();
  }

  if (typeof value !== "string") {
    return null;
  }

  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
};

const toRecord = (value: unknown): Record<string, unknown> =>
  value && typeof value === "object" ? (value as Record<string, unknown>) : {};

const extractFromRecord = (
  source: Record<string, unknown>,
  keys: string[]
): string | null => {
  for (const key of keys) {
    const result = toCleanString(source[key]);
    if (result) return result;
  }
  return null;
};

export function createHttpServer() {
  const app = createApp();
  const server = http.createServer(app);
  const io = new Server(server, {
    cors: { origin: "*", methods: ["GET", "POST"]},
  });
  const connectedUsers = new Map<string, ConnectedUser>();

  io.on("connection", (socket) => {
    console.log(
      `[${new Date().toISOString()}] websocket connected: id=${socket.id}`
    );

    const handshakeRecord = {
      ...toRecord(socket.handshake.query),
      ...toRecord(socket.handshake.auth)
    };

    const userId = 
      extractFromRecord(handshakeRecord, [
        "id",
        "userId",
        "uid",
        "playerId",
        "socketId"
      ]) ?? socket.id;
    const userName =
      extractFromRecord(handshakeRecord, [
        "name",
        "username",
        "displayName",
        "playerName"
      ]) ?? userId;
    const avatarClass = extractFromRecord(handshakeRecord, [
      "avatarClass",
      "avatar",
      "icon"
    ]);

    const user: ConnectedUser = avatarClass
      ? { id: userId, name: userName, avatarClass }
      : { id: userId, name: userName };

    connectedUsers.set(socket.id, user);

    socket.emit("chat:users", Array.from(connectedUsers.values()));
    socket.broadcast.emit("chat:user_joined", user);

    socket.on("disconnect", (reason) => {
      console.log(
        `[${new Date().toISOString()}] websocket disconnected: id=${socket.id}, reason=${reason}`
      );

      const leavingUser = connectedUsers.get(socket.id);
      connectedUsers.delete(socket.id);

      if (leavingUser) {
        socket.broadcast.emit("chat:user_left", leavingUser);
      }
    });

    socket.on("chat:message", (msg: string) => {
      io.emit("chat:message", { from: socket.id, text: msg, at: Date.now() });
    });
  });

  return { app, server, io };
}

if (require.main === module) {
  const { server } = createHttpServer();
  const PORT = process.env.PORT || 4000;
  server.listen(PORT, () => {
    console.log(`Server listening on http://localhost:${PORT}`);
  });
}
