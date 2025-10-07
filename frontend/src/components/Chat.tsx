'use client';

import { useEffect, useState } from 'react';
import { useSharedSocket } from '../hooks/useSharedSocket';

export type ChatMessage = {
  from: string;
  text: string;
};

type ChatProps = {
  socketUrl: string;
  avatarClass?: string;
  className?: string;
};

export function Chat({
  socketUrl,
  avatarClass = 'nes-mario',
  className,
}: ChatProps) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const socket = useSharedSocket(socketUrl);

  useEffect(() => {
    if (!socket) {
      setMessages([]);
      return;
    }

    const handleMessage = (payload: ChatMessage) => {
      setMessages((prev) => [...prev, payload]);
    };

    socket.on('chat:message', handleMessage);

    return () => {
      socket.off('chat:message', handleMessage);
    };
  }, [socket]);

  const sendMessage = () => {
    const msg = input.trim();
    if (!msg || !socket) return;
    socket.emit('chat:message', msg);
    setInput('');
  };

  return (
    <section
      className={[
        'nes-container',
        'with-title',
        'w-full',
        'max-w-md',
        'mx-auto',
        className,
      ]
        .filter(Boolean)
        .join(' ')}
    >
      <p className="title">Chat</p>

      <div className="mb-4 max-h-80 space-y-2 overflow-y-auto bg-white p-2">
        {messages.map((message, index) => (
          <div key={index} className="flex items-start gap-2">
            <i className={avatarClass} aria-hidden="true" />
            <div className="nes-balloon from-left">
              <p className="text-sm">
                {message.from.slice(0, 6)}: {message.text}
              </p>
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
          onChange={(event) => setInput(event.target.value)}
          placeholder="Type a message…"
          onKeyDown={(event) => event.key === 'Enter' && sendMessage()}
        />
        <button className="nes-btn is-primary" onClick={sendMessage}>
          Send
        </button>
      </div>
    </section>
  );
}

export default Chat;
