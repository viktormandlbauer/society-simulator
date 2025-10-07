'use client';

import { useEffect, useMemo, useState } from 'react';
import { useSharedSocket } from '../hooks/useSharedSocket';

type ConnectedUsersProps = {
  socketUrl: string;
  className?: string;
  title?: string;
  fallbackAvatarClass?: string;
};

type ConnectedUser = {
  id: string | null;
  name: string;
  avatarClass?: string | null;
};

const normalizeText = (value: unknown): string | null => {
  const asString =
    typeof value === 'number' ? value.toString() : value;
  if (typeof asString !== 'string') return null;
  const trimmed = asString.trim();
  return trimmed.length > 0 ? trimmed : null;
};

const normalizeUser = (value: unknown): ConnectedUser | null => {
  if (value == null) return null;

  if (typeof value === 'string') {
    const name = normalizeText(value);
    return name ? { id: null, name, avatarClass: null } : null;
  }

  if (typeof value === 'object') {
    const record = value as Record<string, unknown>;
    const id =
      normalizeText(record.id) ??
      normalizeText(record.userId) ??
      normalizeText(record.socketId);
    const name =
      normalizeText(record.name) ??
      normalizeText(record.username) ??
      normalizeText(record.displayName) ??
      id;
    if (!name && !id) return null;

    const avatarClass =
      normalizeText(record.avatarClass) ?? normalizeText(record.avatar);

    return {
      id: id ?? null,
      name: name ?? 'Unknown player',
      avatarClass,
    };
  }

  return null;
};

const upsertUser = (list: ConnectedUser[], next: ConnectedUser): ConnectedUser[] => {
  const key = next.id ?? next.name;
  if (!key) return list;
  const existingIndex = list.findIndex((user) => {
    if (next.id && user.id) return user.id === next.id;
    return user.name === next.name;
  });
  if (existingIndex >= 0) {
    const updated = [...list];
    updated[existingIndex] = next;
    return updated;
  }
  return [...list, next];
};

const removeUser = (list: ConnectedUser[], target: ConnectedUser): ConnectedUser[] => {
  if (target.id) {
    return list.filter((user) => user.id !== target.id);
  }
  if (target.name) {
    return list.filter((user) => user.name !== target.name);
  }
  return list;
};

export function ConnectedUsers({
  socketUrl,
  className,
  title = 'Online players',
  fallbackAvatarClass = 'nes-mario',
}: ConnectedUsersProps) {
  const [users, setUsers] = useState<ConnectedUser[]>([]);
  const socket = useSharedSocket(socketUrl);

  const containerClasses = useMemo(
    () =>
      [
        'nes-container',
        'with-title',
        'w-full',
        'max-w-md',
        'mx-auto',
        className,
      ]
        .filter(Boolean)
        .join(' '),
    [className],
  );

  useEffect(() => {
    if (!socket) {
      setUsers([]);
      return;
    }

    const handleUsers = (payload: unknown) => {
      if (!Array.isArray(payload)) return;
      const normalized = payload
        .map((item) => normalizeUser(item))
        .filter((item): item is ConnectedUser => Boolean(item));

      // Deduplicate by id/name preserving order.
      const deduped: ConnectedUser[] = [];
      normalized.forEach((user) => {
        const key = user.id ?? user.name;
        if (!key) return;
        const alreadyExists = deduped.some((existing) => {
          if (user.id && existing.id) return user.id === existing.id;
          return existing.name === user.name;
        });
        if (!alreadyExists) deduped.push(user);
      });

      setUsers(deduped);
    };

    const handleUserJoined = (payload: unknown) => {
      const next = normalizeUser(payload);
      if (!next) return;
      setUsers((prev) => upsertUser(prev, next));
    };

    const handleUserLeft = (payload: unknown) => {
      const leaving = normalizeUser(payload);
      if (!leaving) return;
      setUsers((prev) => removeUser(prev, leaving));
    };

    socket.on('chat:users', handleUsers);
    socket.on('chat:user_joined', handleUserJoined);
    socket.on('chat:user_left', handleUserLeft);

    return () => {
      socket.off('chat:users', handleUsers);
      socket.off('chat:user_joined', handleUserJoined);
      socket.off('chat:user_left', handleUserLeft);
    };
  }, [socket]);

  const hasUsers = users.length > 0;

  return (
    <section className={containerClasses}>
      <p className="title">{title}</p>

      {hasUsers ? (
        <ul className="space-y-3">
          {users.map((user) => {
            const avatarClass = user.avatarClass || fallbackAvatarClass;
            const key = user.id ?? user.name;
            return (
              <li key={key} className="flex items-center gap-3 text-sm">
                <span className="relative flex h-16 w-16 flex-shrink-0 items-center justify-center overflow-hidden">
                  <i
                    className={`${avatarClass} leading-none`}
                    aria-hidden="true"
                    style={{ transform: 'scale(0.7)', transformOrigin: 'top left' }}
                  />
                </span>
                <span>{user.name}</span>
              </li>
            );
          })}
        </ul>
      ) : (
        <p className="text-center text-sm text-gray-500">
          Waiting for players to join…
        </p>
      )}
    </section>
  );
}

export default ConnectedUsers;
