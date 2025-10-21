'use client';

import { useState } from 'react';
import AvatarSelection, { AvatarOption } from '../components/AvatarSelection';
import Chat from '../components/Chat';
import ConnectedUsers from '../components/ConnectedUsers';

const GAMESTATE_SOCKET_PATH = process.env.GAMESTATE_SOCKET_PATH || "/";

export default function Home() {
  const [confirmedAvatar, setConfirmedAvatar] = useState<AvatarOption | null>(null);

  return (
    <main className="mx-auto flex w-full flex-col items-center gap-6 p-6">
      <h1 className="text-2xl">Society Simulator</h1>

      {confirmedAvatar ? (
        <div className="flex w-full max-w-4xl flex-col items-center gap-4">
          <Chat
            className="w-full"
            socketUrl={GAMESTATE_SOCKET_PATH}
            avatarClass={confirmedAvatar.iconClass}
          />
          <ConnectedUsers className="w-full" socketUrl={GAMESTATE_SOCKET_PATH} />
        </div>
      ) : (
        <>
          <AvatarSelection
            className="w-full max-w-md"
            onConfirm={(avatar) => setConfirmedAvatar(avatar)}
          />
          <section className="nes-container is-dark with-title w-full max-w-md text-center mx-auto">
            <p className="text-sm">Pick an avatar to start chatting.</p>
          </section>
        </>
      )}
    </main>
  );
}
