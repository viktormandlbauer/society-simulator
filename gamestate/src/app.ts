import express from "express";
import cors from "cors";
import path from "path";

export function createApp() {
  const app = express();
  app.use(cors());
  app.use(express.json());

  app.get("/health", (_req, res) => res.json({ ok: true }));

  return app;
}

const BACKEND_URL = process.env.BACKEND_URL || "http://localhost:8080";
const GAMESTATE_SOCKET_PATH = process.env.GAMESTATE_SOCKET_PATH || "http://localhost:4000";