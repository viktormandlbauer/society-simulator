"use client";

import { usePlayerSession } from "@/features/session/PlayerSessionContext";
import { StartPage } from "@/features/session/ui/StartPage";
import { LobbyListPage } from "@/features/lobby/ui/LobbyListPage";

export function RootShell() {
    const { session } = usePlayerSession();

    if (!session) return <StartPage />;

    return <LobbyListPage />;
}
