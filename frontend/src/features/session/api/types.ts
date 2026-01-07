import type {AvatarId} from "@/shared/avatars";

export type GuestSessionRequest = {
    name: string;
    avatarId: AvatarId;
};

export type GuestSessionResponse = {
    playerId: string;
    name: string;
    avatarId: AvatarId;
    token: string;
    expiresAt: string;
    role: "GUEST" | "PLAYER" | "GAMEMASTER";
};