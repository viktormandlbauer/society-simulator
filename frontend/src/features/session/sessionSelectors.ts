import {useSessionStore} from "@/features/session/sessionStore";

export function getAuthToken(): string | null {
    return useSessionStore.getState().session?.token ?? null;
}