"use client"

import {useSessionStore} from "@/features/session/sessionStore";
import {StartPage} from "@/features/session/ui/StartPage";
import {LobbyListPage} from "@/features/lobby/ui/LobbyListPage";
import {useLobbyRuntimeStore} from "@/features/lobby/lobbyRuntimeStore";
import {LobbyPage} from "@/features/lobby/ui/LobbyPage";

export function RootShell() {
    const session = useSessionStore((state) => state.session);
    const hasHydrated = useSessionStore((state) => state.hasHydrated);
    const currentLobby = useLobbyRuntimeStore((state) => state.currentLobby);

    if (!hasHydrated) {
        return (
            <div className="nes-container with-title is-rounded is-dark">
                <p className="title">Society Simulator</p>
                <p>Loading...</p>
            </div>
        );
    }

    if (!session) return <StartPage />;

    if (currentLobby) return <LobbyPage />

    return <LobbyListPage />;
}
