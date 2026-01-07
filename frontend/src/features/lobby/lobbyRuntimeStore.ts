"use client"

import {LobbyViewDto} from "@/features/lobby/api/types";
import {create} from "zustand";

type LobbyRuntimeStore = {
    currentLobby: LobbyViewDto | null;

    enterLobby: (lobby: LobbyViewDto) => void;
    leaveLobbyLocal: () => void;
};

export const useLobbyRuntimeStore = create<LobbyRuntimeStore>((set) => ({
    currentLobby: null,

    enterLobby: (lobby) => set({currentLobby: lobby}),
    leaveLobbyLocal: () => set({currentLobby: null}),
}))