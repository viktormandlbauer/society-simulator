"use client";

import { useEffect, useRef, useState } from "react";
import { io, Socket } from "socket.io-client";

const SOCKET_URL =
  process.env.NEXT_PUBLIC_SOCKET_URL || "http://localhost:3000";

export default function Home() {
  const [messages, setMessages] = useState<{ from: string; text: string }[]>(
    []
  );
  const [input, setInput] = useState("");
  const socketRef = useRef<Socket | null>(null);

  useEffect(() => {
    const s = io(SOCKET_URL, { transports: ["websocket"] });
    socketRef.current = s;

    s.on("chat:message", (payload: { from: string; text: string }) => {
      setMessages((m) => [...m, payload]);
    });

    return () => {
      s.disconnect();
      socketRef.current = null;
    };
  }, []);

  const send = () => {
    const msg = input.trim();
    if (!msg) return;
    socketRef.current?.emit("chat:message", msg);
    setInput("");
  };

  return (
    <main className="container mx-auto max-w-2xl p-6">
      <h1 className="text-2xl mb-4">Next.js + Tailwind + NES.css</h1>

      <section className="nes-container with-title mb-4">
        <p className="title">Chat</p>

        <div className="space-y-2 mb-4 max-h-80 overflow-y-auto p-2 bg-white">
          {messages.map((m, i) => (
            <div key={i} className="flex items-start gap-2">
              <i className="nes-mario"></i>
              <div className="nes-balloon from-left">
                <p className="text-sm">{m.from.slice(0, 6)}: {m.text}</p>
              </div>
            </div>
          ))}
          {messages.length === 0 && (
            <p className="text-sm text-gray-500">No messages yet.</p>
          )}
        </div>

        <div className="flex gap-2">
          <input
            className="nes-input flex-1"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type a message…"
            onKeyDown={(e) => e.key === "Enter" && send()}
          />
          <button className="nes-btn is-primary" onClick={send}>
            Send
          </button>
        </div>
      </section>

      <section className="nes-container is-dark with-title">
        <p className="title">NES.css + Tailwind</p>
        <button className="nes-btn">Normal</button>
        <button className="nes-btn is-primary ml-2">Primary</button>
        <button className="nes-btn is-success ml-2">Success</button>
        <button className="nes-btn is-warning ml-2">Warning</button>
        <button className="nes-btn is-error ml-2">Error</button>
      </section>
    </main>
  );
}
