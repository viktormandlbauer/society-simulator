"use client";

import { useEffect, useState } from "react";
import { useSocket } from "@/app/_lib/useSocket";

type ChatMsg = { text: string; from: string; at: number };
type PongMsg = { msg: string; at: number };

export default function WsTestPage() {
  const { connected, emit, on } = useSocket();
  const [pong, setPong] = useState<PongMsg | null>(null);
  const [input, setInput] = useState("");
  const [chat, setChat] = useState<ChatMsg[]>([]);

  useEffect(() => {
    const offPong = on("test:pong", (p) => setPong(p));
    const offChat = on("test:chat", (m) => setChat((prev) => [m, ...prev]));
    return () => {
      offPong?.();
      offChat?.();
    };
  }, [on]);

  return (
    <main className="container mx-auto p-6 max-w-3xl">
      <section className="nes-container with-title is-rounded">
        <p className="title">WebSocket Test</p>

        {/* Connection status */}
        <div className="mb-4 flex items-center gap-3">
          <span className={`nes-text ${connected ? "" : "is-disabled"}`}>
            <span className={connected ? "nes-text is-primary" : "nes-text is-error"}>
              {connected ? "Connected" : "Connecting..."}
            </span>
          </span>

          <button
            className={`nes-btn ${connected ? "is-primary" : "is-disabled"}`}
            onClick={() => emit("test:ping", { msg: "hello" })}
            disabled={!connected}
            aria-label="send ping"
            title="Send Ping"
          >
            Send Ping
          </button>

          <span className="nes-text is-primary">
            {pong
              ? `Last pong: "${pong.msg}" at ${new Date(pong.at).toLocaleTimeString()}`
              : "No pong yet"}
          </span>
        </div>

        {/* Chat input */}
        <div className="flex items-center gap-2 mb-4">
          <input
            className="nes-input flex-1"
            placeholder="Chat message"
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />
          <button
            className={`nes-btn ${connected ? "is-success" : "is-disabled"}`}
            onClick={() => {
              const text = input.trim();
              if (!connected || !text) return;
              emit("test:chat", { text });
              setInput("");
            }}
            disabled={!connected}
            aria-label="send chat message"
          >
            Send
          </button>
        </div>

        {/* Chat log */}
        <div className="nes-container is-rounded">
          <p className="mb-2">
            <strong>Chat</strong> <span className="text-xs opacity-70">(open a 2nd tab to see broadcast)</span>
          </p>
          <ul className="list-disc pl-5 space-y-1">
            {chat.length === 0 && (
              <li className="opacity-60">No messages yet.</li>
            )}
            {chat.map((m, i) => (
              <li key={i} className="font-mono">
                [{new Date(m.at).toLocaleTimeString()}] {m.from.slice(0, 6)}: {m.text}
              </li>
            ))}
          </ul>
        </div>
      </section>
    </main>
  );
}
