"use client";

import { useEffect } from "react";
import { useSessionStore } from "./sessionStore";

export function useSessionHydration() {
    useEffect(() => {
        useSessionStore.persist.rehydrate();

        useSessionStore.getState().markHydrated();
    }, []);
}
