"use client";

import { NesButton } from "@/shared/ui/NesButton";
import type { LobbyListItemDto } from "@/features/lobby/api/types";

type Props = {
    lobby: LobbyListItemDto;
    isLoading: boolean;
    onJoin: (lobby: LobbyListItemDto) => void | Promise<void>;
};

export function LobbyCard({ lobby, isLoading, onJoin }: Props) {
    const isFull = lobby.playersCount >= lobby.maxPlayers;
    const isJoinable = lobby.status === "OPEN";
    const joinDisabled = isLoading || isFull || !isJoinable;

    return (
        <div className="nes-container is-rounded is-dark">
            <div className="flex items-start justify-between gap-4">
                <div className="flex flex-col gap-1">
                    <p className="text-lg">{lobby.name}</p>
                    <p className="text-sm opacity-80">
                        Theme: {lobby.themeName} • Players: {lobby.playersCount}/{lobby.maxPlayers} • Status: {lobby.status}
                        {lobby.hasPassword ? " • 🔒" : ""}
                        {isFull ? " • FULL" : ""}
                    </p>
                </div>

                <div className="shrink-0">
                    <NesButton
                        variant="success"
                        disabled={joinDisabled}
                        onClick={() => void onJoin(lobby)}
                    >
                        Join
                    </NesButton>
                </div>
            </div>
        </div>
    );
}
