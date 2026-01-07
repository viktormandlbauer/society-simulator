"use client";

import {ReactNode} from "react";
import {useSessionHydration} from "@/features/session/useSessionHydration";

export function Providers({children}: { children: ReactNode }) {
    useSessionHydration();
    return children
}