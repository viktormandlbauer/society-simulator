import type { Server, Socket } from "socket.io";
import type { ClientToServerEvents, ServerToClientEvents, SocketData } from "./types";

type IO = Server<ClientToServerEvents, ServerToClientEvents, {}, SocketData>;
type S = Socket<ClientToServerEvents, ServerToClientEvents, {}, SocketData>;

export function registerTestHandlers(io: IO, socket: S) {
  // Echo back only to the sender
  socket.on("test:ping", ({ msg }) => {
    socket.emit("test:pong", { msg: `pong: ${msg}`, at: Date.now() });
  });

  // Broadcast chat message to everyone (simple smoke test)
  socket.on("test:chat", ({ text }) => {
    io.emit("test:chat", { text, from: socket.id, at: Date.now() });
  });
}
