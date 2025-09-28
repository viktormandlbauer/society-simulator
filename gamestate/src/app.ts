import express from "express";
import cors from "cors";
import path from "path";

export function createApp() {
  const app = express();
  app.use(cors());
  app.use(express.json());
  app.use(express.static(path.join(__dirname, "..", "public")));

  app.get("/health", (_req, res) => res.json({ ok: true }));

  return app;
}
