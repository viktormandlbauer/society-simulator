"use client";

import { useMemo, useState, useEffect } from "react";
import { NesButton } from "@/shared/ui/NesButton";
import { useSessionStore } from "@/features/session/sessionStore";
import { useLobbyRuntimeStore } from "@/features/lobby/lobbyRuntimeStore";
import { leaveLobby as leaveLobbyApi, startGameFromLobby } from "@/features/lobby/api/lobbies";
import type { ProblemDetails } from "@/shared/http/problemDetails";
import { asProblemDetails, getProblemMessage } from "@/shared/http/problemDetails";
import { useRouter } from "next/navigation";
import { useLobbyChat } from "@/features/lobby/hooks/useLobbyChat";
import { LobbyChat } from "@/features/lobby/ui/components/LobbyChat";

export function LobbyPage() {
    const router = useRouter();
    const session = useSessionStore((s) => s.session);
    const lobby = useLobbyRuntimeStore((s) => s.currentLobby);
    const leaveLobbyLocal = useLobbyRuntimeStore((s) => s.leaveLobbyLocal);

    const [isLoading, setIsLoading] = useState(false);
    const [isStarting, setIsStarting] = useState(false);
    const [error, setError] = useState<ProblemDetails | null>(null);

    // Initialize lobby chat
    const { messages, isConnected, error: chatError, sendMessage, onGameStarted } = useLobbyChat(
        session?.token ?? null,
        lobby?.lobbyId ?? null
    );

    // Listen for game started event and navigate to game page
    useEffect(() => {
        onGameStarted((gameId: string) => {
            console.log("Navigating to game:", gameId);
            router.push(`/game/${gameId}`);
        });
    }, [onGameStarted, router]);

    const membersSorted = useMemo(() => {
        if (!lobby) return [];
        // Put gamemaster first, rest keep stable order (joinedAt)
        return [...lobby.members].sort((a, b) => {
            if (a.role === "GAMEMASTER" && b.role !== "GAMEMASTER") return -1;
            if (b.role === "GAMEMASTER" && a.role !== "GAMEMASTER") return 1;
            return a.joinedAt.localeCompare(b.joinedAt);
        });
    }, [lobby]);

    const isGamemaster = useMemo(() => {
        if (!session || !lobby) return false;
        const member = lobby.members.find(m => m.playerId === session.playerId);
        return member?.role === "GAMEMASTER";
    }, [session, lobby]);

    const handleStartGame = async () => {
        if (!session || !lobby) return;

        setError(null);
        setIsStarting(true);

        try {
            const gameId = await startGameFromLobby(session.token, lobby.lobbyId);
            // Navigate to the game page
            router.push(`/game/${gameId}`);
        } catch (e: unknown) {
            const p = asProblemDetails(e);
            setError(p ?? { title: "Error", detail: "Failed to start game." });
        } finally {
            setIsStarting(false);
        }
    };

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

                    <LobbyChat
                        messages={messages}
                        isConnected={isConnected}
                        error={chatError}
                        onSendMessage={sendMessage}
                    />

                    {isGamemaster && (
                        <NesButton
                            variant="success"
                            disabled={isStarting || isLoading}
                            onClick={handleStartGame}
                        >
                            {isStarting ? "Starting..." : "Start Game"}
                        </NesButton>
                    )}

                    <NesButton
                        variant="warning"
                        disabled={isLoading || isStarting}
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
                            <p className="mb-2 font-bold">Welcome to Society Simulator!</p>

                            <p className="text-xs opacity-70 mb-2">
                                Step into a living, evolving society where every choice matters. Your decisions will shape communities, influence culture, and determine the future of the world you create.
                            </p>

                            <p className="text-xs opacity-70 mb-2">
                                How to play:
                            </p>

                            <p className="text-xs opacity-70">
                                You’ll be presented with a scenario. Review the available options and vote for the one you believe is best. Once voting ends, the outcome shapes what happens next and a new scenario appears.
                            </p>
                        </div>
                </div>
            </div>
        </div>
    );
}
