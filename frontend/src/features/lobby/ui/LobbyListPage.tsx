"use client";

import { useEffect, useMemo, useState } from "react";
import {clearSessionAndStorage, useSessionStore} from "@/features/session/sessionStore";

import type { LobbyListItemDto, CreateLobbyRequestDto } from "@/features/lobby/api/types";
import { createLobby, getLobbies, joinLobby } from "@/features/lobby/api/lobbies";

import type { ThemeDto } from "@/features/themes/api/types";
import { getThemes } from "@/features/themes/api/themes";

import type { ProblemDetails } from "@/shared/http/problemDetails";
import { asProblemDetails, getProblemMessage } from "@/shared/http/problemDetails";

import { NesButton } from "@/shared/ui/NesButton";

import { LobbyListHeader } from "./components/LobbyListHeader";
import { LobbyCard } from "./components/LobbyCard";
import { JoinLobbyModal } from "./modals/JoinLobbyModal";
import { CreateLobbyModal } from "./modals/CreateLobbyModal";
import {useLobbyRuntimeStore} from "@/features/lobby/lobbyRuntimeStore";

type JoinTarget = {
    lobbyId: string;
    lobbyName: string;
};

export function LobbyListPage() {
    const session = useSessionStore((s) => s.session);
    const enterLobby = useLobbyRuntimeStore((s) => s.enterLobby);

    const token = session?.token ?? null;

    const [lobbies, setLobbies] = useState<LobbyListItemDto[]>([]);
    const [themes, setThemesState] = useState<ThemeDto[]>([]);

    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState<ProblemDetails | null>(null);

    // Join modal state
    const [joinTarget, setJoinTarget] = useState<JoinTarget | null>(null);
    const [joinError, setJoinError] = useState<ProblemDetails | null>(null);

    // Create modal state
    const [showCreate, setShowCreate] = useState(false);
    const [createError, setCreateError] = useState<ProblemDetails | null>(null);

    const sessionVm = useMemo(() => {
        if (!session) return null;
        return { name: session.name, avatarId: session.avatarId };
    }, [session]);

    async function loadLobbies() {
        if (!token) return;

        setApiError(null);
        setIsLoading(true);

        try {
            const data = await getLobbies(token);
            setLobbies(data);
        } catch (e: unknown) {
            const p = asProblemDetails(e);
            setApiError(p ?? { title: "Error", detail: "Failed to load lobbies." });
        } finally {
            setIsLoading(false);
        }
    }

    async function loadThemesIfNeeded() {
        if (!token) return;
        if (themes.length > 0) return;

        try {
            const t = await getThemes(token);
            setThemesState(t);
        } catch (e: unknown) {
            const p = asProblemDetails(e);
            setApiError(p ?? { title: "Error", detail: "Failed to load themes." });
        }
    }

    useEffect(() => {
        // Load lobbies whenever a (new) session is available
        if (!token) return;
        void loadLobbies();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [token]);

    if (!session || !sessionVm) return null;

    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Lobby List</p>

            <div className="flex flex-col gap-4">
                <LobbyListHeader
                    session={sessionVm}
                    isLoading={isLoading}
                    onRefresh={loadLobbies}
                    onLeaveSession={() => clearSessionAndStorage()}
                />

                {apiError && <p className="nes-text is-error text-xs">{getProblemMessage(apiError)}</p>}

                {!isLoading && lobbies.length === 0 && (
                    <div className="nes-container is-rounded is-dark">
                        <p>No lobbies yet. Create one to get started.</p>
                    </div>
                )}

                {!isLoading && lobbies.length > 0 && (
                    <div className="flex flex-col gap-3">
                        {lobbies.map((lobby) => (
                            <LobbyCard
                                key={lobby.lobbyId}
                                lobby={lobby}
                                isLoading={isLoading}
                                onJoin={async (target) => {
                                    if (!token) return;

                                    setApiError(null);
                                    setJoinError(null);

                                    if (target.hasPassword) {
                                        setJoinTarget({ lobbyId: target.lobbyId, lobbyName: target.name });
                                        return;
                                    }

                                    setIsLoading(true);
                                    try {
                                        const view = await joinLobby(token, target.lobbyId);
                                        enterLobby(view);
                                    } catch (e: unknown) {
                                        const p = asProblemDetails(e);
                                        setApiError(p ?? { title: "Error", detail: "Failed to join lobby." });
                                    } finally {
                                        setIsLoading(false);
                                    }
                                }}
                            />
                        ))}
                    </div>
                )}

                <div className="pt-2">
                    <NesButton
                        variant="primary"
                        disabled={isLoading}
                        onClick={async () => {
                            setApiError(null);
                            setCreateError(null);
                            setShowCreate(true);
                            await loadThemesIfNeeded();
                        }}
                    >
                        Create Lobby
                    </NesButton>
                </div>
            </div>

            <JoinLobbyModal
                open={joinTarget !== null}
                lobbyName={joinTarget?.lobbyName ?? ""}
                isLoading={isLoading}
                error={joinError}
                onCancel={() => {
                    setJoinTarget(null);
                    setJoinError(null);
                }}
                onJoin={async (password) => {
                    if (!token || !joinTarget) return;

                    setIsLoading(true);
                    setJoinError(null);

                    try {
                        const view = await joinLobby(token, joinTarget.lobbyId, { password });
                        setJoinTarget(null);
                        enterLobby(view);
                    } catch (e: unknown) {
                        const p = asProblemDetails(e);
                        setJoinError(p ?? { title: "Error", detail: "Failed to join lobby." });
                    } finally {
                        setIsLoading(false);
                    }
                }}
            />

            <CreateLobbyModal
                open={showCreate}
                themes={themes}
                isLoading={isLoading}
                error={createError}
                onCancel={() => {
                    setShowCreate(false);
                    setCreateError(null);
                }}
                onCreate={async (req: CreateLobbyRequestDto) => {
                    if (!token) return;

                    setIsLoading(true);
                    setCreateError(null);

                    try {
                        const view = await createLobby(token, req);
                        setShowCreate(false);
                        enterLobby(view);
                    } catch (e: unknown) {
                        const p = asProblemDetails(e);
                        setCreateError(p ?? { title: "Error", detail: "Failed to create lobby." });
                    } finally {
                        setIsLoading(false);
                    }
                }}
            />
        </div>
    );
}
