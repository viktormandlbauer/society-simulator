"use client";

import {ReactNode} from "react";
import {PlayerSessionProvider} from "@/features/player/PlayerSessionContext";

export function Providers({children}: { children: ReactNode }) {
    return (
        <PlayerSessionProvider>
            {children}
        </PlayerSessionProvider>
    )
}