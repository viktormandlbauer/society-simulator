"use client"

import {useSessionStore} from "@/features/session/sessionStore";
import {StartPage} from "@/features/session/ui/StartPage";
import {LobbyListPage} from "@/features/lobby/ui/LobbyListPage";

export function RootShell() {
    const session = useSessionStore((state) => state.session);
    const hasHydrated = useSessionStore((state) => state.hasHydrated);

    if (!hasHydrated) {
        return (
            <div className="nes-container with-title is-rounded is-dark">
                <p className="title">Society Simulator</p>
                <p>Loading...</p>
            </div>
        );
    }

    if (!session) return <StartPage />;

    return <LobbyListPage />;
}
