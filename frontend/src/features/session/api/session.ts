import type { AvatarId } from "@/shared/avatars";
import type { ProblemDetails } from "@/shared/http/problemDetails";
import { ProblemDetailsError } from "@/shared/http/problemDetails";

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

export async function createGuestSession(req: GuestSessionRequest): Promise<GuestSessionResponse> {
    const res = await fetch("/api/session/guest", {
        method: "POST",
        headers: {"Content-Type": "application/json", Accept: "application/json"},
        body: JSON.stringify(req),
    });

    const text = await res.text();
    const data = text ? safeJsonParse(text) : null;

    if (!res.ok) {
        const problem = (data ?? {title: "Request failed", status: res.status}) as ProblemDetails;
        throw new ProblemDetailsError(problem);
    }

    if (!data || typeof data !== "object") {
        throw new ProblemDetailsError({
            title: "Invalid Response",
            status: 502,
            detail: "Session endpoint returned non-JSON or empty body.",
        });
    }

    const obj = data as Partial<GuestSessionResponse>;
    if (!obj.token || !obj.playerId) {
        throw new ProblemDetailsError({
            title: "Invalid Response",
            status: 502,
            detail: "Session endpoint response is missing required fields.",
        });
    }

    return obj as GuestSessionResponse;
}

function safeJsonParse(text: string) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}
