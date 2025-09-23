"use client";

import { useEffect, useRef, useState } from "react";
import { io, Socket } from "socket.io-client";
import type {
  ClientToServerEvents,
  ServerToClientEvents,
} from "@/ws/types";

// Helper types
type EmitArgs<K extends keyof ClientToServerEvents> =
  ClientToServerEvents[K] extends (...args: infer A) => any ? A : never;

type Handler<K extends keyof ServerToClientEvents> =
  (...args: Parameters<ServerToClientEvents[K]>) => void;

export function useSocket() {
  const socketRef = useRef<Socket<ServerToClientEvents, ClientToServerEvents> | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Explicitly type the socket so TS knows our event maps
    const socket: Socket<ServerToClientEvents, ClientToServerEvents> = io("", {
      path: "/ws",
      transports: ["websocket"],
      autoConnect: true,
    });

    socketRef.current = socket;

    const onConnect = () => setConnected(true);
    const onDisconnect = () => setConnected(false);

    socket.on("connect", onConnect);
    socket.on("disconnect", onDisconnect);

    return () => {
      socket.off("connect", onConnect);
      socket.off("disconnect", onDisconnect);
      socket.disconnect();
    };
  }, []);

  // Typed emit: spreads the correct args for each event
  function emit<K extends keyof ClientToServerEvents>(ev: K, ...args: EmitArgs<K>) {
    socketRef.current?.emit(ev, ...args);
  }

  // Typed on/off: handler signature matches the server-to-client event
  function on<K extends keyof ServerToClientEvents>(ev: K, handler: Handler<K>) {
    socketRef.current?.on(ev, handler as any);      // cast helps with Socket.IO's variadic typing
    return () => socketRef.current?.off(ev, handler as any);
  }

  return { socket: socketRef.current, connected, emit, on };
}
