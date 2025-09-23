import { Server } from "socket.io";
import type { Server as HttpServer } from "node:http";
import type {
  ClientToServerEvents,
  ServerToClientEvents,
  InterServerEvents,
  SocketData,
} from "./types";

import { registerLobbyHandlers } from "./lobby";
import { registerTestHandlers } from "./test";

export function setupSocket(httpServer: HttpServer) {
  const io = new Server<
    ClientToServerEvents,
    ServerToClientEvents,
    InterServerEvents,
    SocketData
  >(httpServer, {
    path: "/ws",
    cors: { origin: "*" },
    pingInterval: 25_000,
    pingTimeout: 20_000,
  });

  io.on("connection", (socket) => {
    console.log("⚡ client connected", socket.id);

    registerLobbyHandlers(io, socket);
    registerTestHandlers(io, socket);

    socket.on("disconnect", () => {
      console.log("❌ client disconnected", socket.id);
    });
  });

  return io;
}
