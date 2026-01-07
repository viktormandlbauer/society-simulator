import {fetchJson} from "@/shared/http/fetchJson";
import {GuestSessionRequest, GuestSessionResponse} from "@/features/session/api/types";

export async function createGuestSession(req: GuestSessionRequest): Promise<GuestSessionResponse> {
    return fetchJson<GuestSessionResponse>("/api/session/guest", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
        },
        body: JSON.stringify(req),
        cache: "no-store",
    });
}
