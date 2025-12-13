import type { AvatarId } from "@/lib/avatars";
import type { ProblemDetails } from "./problemDetails";

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
};

export async function createGuestSession(req: GuestSessionRequest): Promise<GuestSessionResponse> {
    const res = await fetch("/api/session/guest", {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        body: JSON.stringify(req),
    });

    const text = await res.text();
    const data = text ? safeJsonParse(text) : null;

    if (!res.ok) {
        throw (data ?? { title: "Request failed", status: res.status }) as ProblemDetails;
    }

    return data as GuestSessionResponse;
}

function safeJsonParse(text: string) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}
