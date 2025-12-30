import {AvatarId} from "@/shared/avatars";

export type PlayerRole = "GUEST" | "PLAYER" | "GAMEMASTER";

export interface PlayerSession {
    playerId: string;
    name: string;
    avatarId: AvatarId;
    token: string;
    expiresAt: string;
    role: PlayerRole;
}