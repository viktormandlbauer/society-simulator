import type { Server, Socket } from "socket.io";
import type { ClientToServerEvents, ServerToClientEvents, SocketData } from "./types";

type TypedIO = Server<ClientToServerEvents, ServerToClientEvents, {}, SocketData>;
type TypedSocket = Socket<ClientToServerEvents, ServerToClientEvents, {}, SocketData>;

export function registerLobbyHandlers(io: TypedIO, socket: TypedSocket) {
  socket.on("lobby:join", ({ lobbyId, name }) => {
    if (!lobbyId) return;
    socket.data.lobbyId = lobbyId;
    socket.data.name = name;
    socket.join(lobbyId);
    io.to(lobbyId).emit("lobby:presence", { id: socket.id, name, type: "join" });
  });

  socket.on("lobby:leave", ({ lobbyId }) => {
    if (!lobbyId) return;
    socket.leave(lobbyId);
    io.to(lobbyId).emit("lobby:presence", { id: socket.id, name: socket.data.name, type: "leave" });
    if (socket.data.lobbyId === lobbyId) socket.data.lobbyId = undefined;
  });

  socket.on("lobby:vote", ({ lobbyId, choice }) => {
    if (!lobbyId || !choice) return;
    io.to(lobbyId).emit("lobby:voteUpdate", {
      userId: socket.id,
      choice,
      ts: Date.now(),
    });
  });

  socket.on("game:action", ({ lobbyId, action }) => {
    if (!lobbyId) return;
    io.to(lobbyId).emit("game:update", { action, by: socket.id, ts: Date.now() });
  });
}
