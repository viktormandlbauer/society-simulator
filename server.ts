import { createServer } from "node:http";
import next from "next";
import { setupSocket } from "./ws/index";

const dev = process.env.NODE_ENV !== "production";
const app = next({ dev });
const handle = app.getRequestHandler();
const port = Number(process.env.PORT) || 3000;

app.prepare().then(() => {
  const httpServer = createServer((req, res) => handle(req, res));

  // Attach Socket.IO
  setupSocket(httpServer);

  httpServer.listen(port, () => {
    console.log(`> Ready on http://localhost:${port}`);
  });
});
