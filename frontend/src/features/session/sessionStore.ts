"use client"

import type {PlayerSession} from "@/features/session/playerSessionTypes";
import {create} from "zustand";
import {createJSONStorage, persist} from "zustand/middleware";

type SessionState = {
    session: PlayerSession | null;

    // Check if the store has been loaded from persistent storage
    hasHydrated: boolean;

    // Actions to modify the state
    setSession: (session: PlayerSession) => void;
    clearSession: () => void;

    // Retrieve the current session
    getSession: () => PlayerSession | null;

    // Mark the store as hydrated meaning it has loaded from persistent storage
    markHydrated: () => void;
};

export const useSessionStore = create<SessionState>()(
    persist(
        (set, get) => ({
            session: null,
            hasHydrated: false,

            setSession: (session) => set({session}),
            clearSession: () => set({session: null}),

            getSession: () => get().session,

            markHydrated: () => set({hasHydrated: true}),
        }),
        {
            name: "society.session.v1",

            // localStorage is only available in the browser
            storage: createJSONStorage(() => {
                if (typeof window === "undefined") {
                    return {
                        getItem: () => null,
                        setItem: () => {},
                        removeItem: () => {},
                    };
                }
                return window.localStorage;
            }),

            // Trigger hydration manually after mount to prevent UI flicker on initial load
            skipHydration: true,

            // Only persist what we need
            partialize: (state) => ({session: state.session}),
        }
    )
);