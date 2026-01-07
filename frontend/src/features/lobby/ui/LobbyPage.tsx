"use client";

import { useMemo, useState } from "react";
import { NesButton } from "@/shared/ui/NesButton";
import { useSessionStore } from "@/features/session/sessionStore";
import { useLobbyRuntimeStore } from "@/features/lobby/lobbyRuntimeStore";
import { leaveLobby as leaveLobbyApi } from "@/features/lobby/api/lobbies";
import type { ProblemDetails } from "@/shared/http/problemDetails";
import { asProblemDetails, getProblemMessage } from "@/shared/http/problemDetails";

export function LobbyPage() {
    const session = useSessionStore((s) => s.session);
    const lobby = useLobbyRuntimeStore((s) => s.currentLobby);
    const leaveLobbyLocal = useLobbyRuntimeStore((s) => s.leaveLobbyLocal);

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<ProblemDetails | null>(null);

    const membersSorted = useMemo(() => {
        if (!lobby) return [];
        // Put gamemaster first, rest keep stable order (joinedAt)
        return [...lobby.members].sort((a, b) => {
            if (a.role === "GAMEMASTER" && b.role !== "GAMEMASTER") return -1;
            if (b.role === "GAMEMASTER" && a.role !== "GAMEMASTER") return 1;
            return a.joinedAt.localeCompare(b.joinedAt);
        });
    }, [lobby]);

    if (!session || !lobby) return null;

    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">{lobby.name}</p>

            {error && <p className="nes-text is-error text-xs mb-3">{getProblemMessage(error)}</p>}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {/* LEFT: members + chat */}
                <div className="md:col-span-1 flex flex-col gap-4">
                    <div className="nes-container is-rounded is-dark">
                        <p className="mb-2">Players</p>

                        <div className="flex flex-col gap-2">
                            {membersSorted.map((m) => (
                                <div
                                    key={m.playerId}
                                    className={`flex items-center justify-between gap-2 ${
                                        m.role === "GAMEMASTER" ? "border border-yellow-400/60 p-2 rounded" : ""
                                    }`}
                                >
                                    <div className="flex items-center gap-2">
                    <span className={m.role === "GAMEMASTER" ? "nes-text is-warning" : ""}>
                      {m.name}
                    </span>
                                        {m.role === "GAMEMASTER" && <span className="text-xs opacity-80">(GM)</span>}
                                    </div>

                                    <span className="text-xs opacity-80">{m.ready ? "Ready" : "Not ready"}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="nes-container is-rounded is-dark">
                        <p className="mb-2">Chat</p>
                        <div className="h-40 text-xs opacity-70">
                            Chat placeholder (WebSocket later)
                        </div>
                    </div>

                    <NesButton
                        variant="warning"
                        disabled={isLoading}
                        onClick={async () => {
                            setError(null);
                            setIsLoading(true);

                            try {
                                await leaveLobbyApi(session.token, lobby.lobbyId);
                                leaveLobbyLocal();
                            } catch (e: unknown) {
                                const p = asProblemDetails(e);
                                setError(p ?? { title: "Error", detail: "Failed to leave lobby." });
                            } finally {
                                setIsLoading(false);
                            }
                        }}
                    >
                        Leave Lobby
                    </NesButton>
                </div>

                {/* RIGHT: AI output */}
                <div className="md:col-span-2">
                    <div className="nes-container is-rounded is-dark h-full min-h-[320px]">
                        <p className="mb-2">AI Output</p>
                        <div className="text-xs opacity-70">
                            AI narrative placeholder (DeepInfra integration later)
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
