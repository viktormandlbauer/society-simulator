import {CreateLobbyRequestDto, JoinLobbyRequestDto, LobbyListItemDto, LobbyViewDto} from "@/features/lobby/api/types";
import {fetchJson, fetchNoContent} from "@/shared/http/fetchJson";

export function getLobbies(token: string): Promise<LobbyListItemDto[]> {
    return fetchJson<LobbyListItemDto[]>("/api/lobbies", {
        method: "GET",
        headers: {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        cache: "no-store",
    });
}

export function createLobby(token: string, req: CreateLobbyRequestDto): Promise<LobbyViewDto> {
    return fetchJson<LobbyViewDto>("/api/lobbies", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(req),
        cache: "no-store"
    });
}

export function joinLobby(
    token: string,
    lobbyId: string,
    req?: JoinLobbyRequestDto
): Promise<LobbyViewDto> {
    return fetchJson<LobbyViewDto>(`/api/lobbies/${lobbyId}/join`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(req ?? {}),
        cache: "no-store"
    });
}

export function leaveLobby(token: string, lobbyId: string): Promise<void> {
    return fetchNoContent(`/api/lobbies/${lobbyId}/leave`, {
        method: "POST",
        headers: {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        cache: "no-store"
    });
}

/**
 * Starts a game from a lobby (gamemaster only).
 * Creates a game and starts it, returning the game ID.
 */
export function startGameFromLobby(token: string, lobbyId: string): Promise<string> {
    return fetchJson<{status: string; data: {gameId: string}}>(`/api/lobbies/${lobbyId}/start`, {
        method: "POST",
        headers: {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        cache: "no-store"
    }).then(response => response.data.gameId);
}