import { useEffect, useState } from 'react';
import { io, Socket } from 'socket.io-client';

type SocketRecord = {
  socket: Socket;
  subscribers: number;
};

const socketRegistry = new Map<string, SocketRecord>();

const ensureSocket = (url: string): SocketRecord => {
  const existing = socketRegistry.get(url);
  if (existing) {
    return existing;
  }

  const socket = io(url, { transports: ['websocket'] });
  const record = { socket, subscribers: 0 };
  socketRegistry.set(url, record);
  return record;
};

const releaseSocket = (url: string) => {
  const record = socketRegistry.get(url);
  if (!record) {
    return;
  }

  record.subscribers -= 1;
  if (record.subscribers <= 0) {
    record.socket.disconnect();
    socketRegistry.delete(url);
  }
};

/**
 * Returns a socket.io client instance that is shared across components
 * subscribing to the same URL. The socket is created lazily and disconnected
 * when the last subscriber unsubscribes.
 */
export const useSharedSocket = (url: string | null | undefined): Socket | null => {
  const [socket, setSocket] = useState<Socket | null>(() => {
    if (!url) return null;
    return socketRegistry.get(url)?.socket ?? null;
  });

  useEffect(() => {
    if (!url) {
      setSocket(null);
      return;
    }

    const record = ensureSocket(url);
    record.subscribers += 1;
    setSocket(record.socket);

    return () => {
      releaseSocket(url);
      setSocket((current) => (current === record.socket ? null : current));
    };
  }, [url]);

  return socket;
};
