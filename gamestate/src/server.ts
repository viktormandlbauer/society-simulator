import http from "http";
import { Server } from "socket.io";
import { createApp } from "./app";

export function createHttpServer() {
  const app = createApp();
  const server = http.createServer(app);
  const io = new Server(server, {
    cors: { origin: "*", methods: ["GET", "POST"] }
  });

  io.on("connection", (socket) => {
    console.log(
      `[${new Date().toISOString()}] websocket connected: id=${socket.id}`
    );

    socket.on("disconnect", (reason) => {
      console.log(
        `[${new Date().toISOString()}] websocket disconnected: id=${socket.id}, reason=${reason}`
      );
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
    console.log(`🚀 Server listening on http://localhost:${PORT}`);
  });
}
