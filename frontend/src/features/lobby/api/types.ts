import {AvatarId} from "@/shared/avatars";

export type LobbyStatus = "OPEN" | "IN_GAME" | "CLOSED";
export type LobbyRole = "GAMEMASTER" | "PLAYER";

export type LobbyListItemDto = {
    lobbyId: string;
    name: string;
    themeId: string;
    themeName: string;
    playersCount: number;
    maxPlayers: number;
    hasPassword: boolean;
    status: LobbyStatus;
};

export type CreateLobbyRequestDto = {
    name: string;
    themeId: string;
    maxPlayers: number;
    maxRounds: number;
    password?: string;
}

export type JoinLobbyRequestDto = {
    password?: string | null;
};

export type LobbyMemberViewDto = {
    playerId: string;
    name: string;
    avatarId: AvatarId;
    joinedAt: string; // ISO date string
    role: LobbyRole;
    ready: boolean;
};

export type LobbyViewDto = {
    lobbyId: string;
    name: string;
    themeId: string;
    themeName: string;
    maxPlayers: number;
    maxRounds: number;
    hasPassword: boolean;
    status: LobbyStatus;
    members: LobbyMemberViewDto[];
}