"use client";

import {PlayerSession} from "@/features/session/playerSessionTypes";
import {createContext, ReactNode, useContext, useMemo, useState} from "react";

type PlayerSessionContextValue = {
    session: PlayerSession | null;
    setSession: (session: PlayerSession | null) => void;
};

const PlayerSessionContext =
    createContext<PlayerSessionContextValue | undefined>(undefined);

export function PlayerSessionProvider({ children }: {children: ReactNode}) {
    const [session, setSession] = useState<PlayerSession | null>(null);

    const value = useMemo(() => ({session, setSession}), [session]);

    return (
        <PlayerSessionContext.Provider value={value}>
            {children}
        </PlayerSessionContext.Provider>
    )
}

export function usePlayerSession() {
    const ctx = useContext(PlayerSessionContext);
    if (!ctx) {
        throw new Error("usePlayerSession must be used within PlayerSessionProvider");
    }
    return ctx;
}