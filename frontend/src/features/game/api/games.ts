import { fetchJson } from "@/shared/http/fetchJson";
import type { ApiResponse, Dilemma, VoteRequest, VoteResult } from "./types";

export async function getCurrentDilemma(token: string, gameId: string): Promise<Dilemma> {
    const response = await fetchJson<ApiResponse<Dilemma>>(
        `/api/games/${gameId}/dilemma`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );
    return response.data;
}

export async function submitVote(
    token: string,
    gameId: string,
    voteRequest: VoteRequest
): Promise<VoteResult> {
    const response = await fetchJson<ApiResponse<VoteResult>>(
        `/api/games/${gameId}/dilemma/vote`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(voteRequest),
        }
    );
    return response.data;
}

export async function getFinalOutcome(token: string, gameId: string): Promise<FinalOutcome> {
    const response = await fetchJson<ApiResponse<FinalOutcome>>(
        `/api/games/${gameId}/outcome`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );
    return response.data;
}

export type FinalOutcome = {
    gameId: string;
    totalRounds: number;
    finalSummary: string;
    roundSummaries: RoundSummary[];
    totalVotesByChoice: Record<number, number>;
};

export type RoundSummary = {
    roundNumber: number;
    dilemmaTitle: string;
    voteCounts: Record<number, number>;
    winningChoiceId: number | null;
};
