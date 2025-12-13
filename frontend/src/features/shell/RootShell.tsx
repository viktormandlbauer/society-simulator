"use client";

import { usePlayerSession } from "@/features/player/PlayerSessionContext";
import { StartPage } from "@/features/pages/StartPage";
import { LobbyListPage } from "@/features/pages/LobbyListPage";

export function RootShell() {
    const { session } = usePlayerSession();

    if (!session) return <StartPage />;

    return <LobbyListPage />;
}
