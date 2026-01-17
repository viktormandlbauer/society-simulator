import { useState, useEffect, useCallback } from "react";
import { getCurrentDilemma, submitVote, getFinalOutcome, type FinalOutcome } from "@/features/game/api/games";
import type { Dilemma, VoteResult } from "@/features/game/api/types";
import { useSessionStore } from "@/features/session/sessionStore";

interface UseDilemmaOptions {
    gameId: string;
    playerId: string;
    onVoteComplete?: (result: VoteResult) => void;
}

/**
 * Custom hook for managing dilemma state and voting.
 * Provides a clean interface for components to interact with the dilemma API.
 */
export function useDilemma({ gameId, playerId, onVoteComplete }: UseDilemmaOptions) {
    const [dilemma, setDilemma] = useState<Dilemma | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [isGameOver, setIsGameOver] = useState(false);
    const [finalOutcome, setFinalOutcome] = useState<FinalOutcome | null>(null);
    const [hasVoted, setHasVoted] = useState(false);
    const [currentRoundNumber, setCurrentRoundNumber] = useState<number | null>(null);
    const [isFetchingOutcome, setIsFetchingOutcome] = useState(false);
    const token = useSessionStore((state) => state.session?.token ?? null);

    const fetchDilemma = useCallback(async () => {
        if (!token) {
            setError("You must be logged in to view dilemmas.");
            setIsLoading(false);
            return;
        }

        try {
            setIsLoading(true);
            setError(null);
            const data = await getCurrentDilemma(token, gameId);
            setDilemma(data);
            setIsGameOver(false);

            // Reset vote status when a new dilemma is loaded
            setHasVoted(false);
            setCurrentRoundNumber(data.id);
        } catch (err) {
            console.error("Failed to fetch dilemma:", err);

            // Check if the error indicates the game is over
            const errorObj = err as { problem?: { status?: number; detail?: string } };
            if (errorObj?.problem?.status === 400 || errorObj?.problem?.detail?.includes("not yet completed")) {
                // Game might be over, try to fetch final outcome
                try {
                    const outcome = await getFinalOutcome(token, gameId);
                    setFinalOutcome(outcome);
                    setIsGameOver(true);
                    setError(null);
                } catch (outcomeErr) {
                    console.error("Failed to fetch final outcome:", outcomeErr);
                    setError("Failed to load game state. Please try again.");
                }
            } else {
                setError("Failed to load dilemma. Please try again.");
            }
        } finally {
            setIsLoading(false);
        }
    }, [gameId, token]);

    const submitChoice = useCallback(async (choiceId: number) => {
        if (!token) {
            setError("You must be logged in to vote.");
            return;
        }

        // Prevent double voting
        if (hasVoted) {
            console.warn("Player has already voted in this round");
            setError("You have already voted in this round.");
            return;
        }

        try {
            setIsSubmitting(true);
            setError(null);
            const result = await submitVote(token, gameId, {
                playerId,
                choiceId,
            });

            // Mark as voted for this round
            setHasVoted(true);

            // Check if game is over (no next dilemma and round completed)
            console.log("Vote result:", result);
            console.log("Round completed:", result.roundCompleted);
            console.log("Next dilemma:", result.nextDilemma);

            if (result.roundCompleted && !result.nextDilemma) {
                // Game is over, fetch final outcome with retry logic
                console.log("Game is over! Fetching final outcome...");
                setIsFetchingOutcome(true);

                // Wait a bit for the backend to finish processing
                await new Promise(resolve => setTimeout(resolve, 1000));

                // Retry logic for fetching final outcome
                let retries = 3;
                let outcome = null;

                while (retries > 0 && !outcome) {
                    try {
                        outcome = await getFinalOutcome(token, gameId);
                        console.log("Final outcome received:", outcome);
                        setFinalOutcome(outcome);
                        setIsGameOver(true);
                        break;
                    } catch (outcomeErr) {
                        retries--;
                        console.warn(`Failed to fetch final outcome, retries left: ${retries}`, outcomeErr);

                        if (retries > 0) {
                            // Wait before retrying (exponential backoff)
                            await new Promise(resolve => setTimeout(resolve, 1500 * (4 - retries)));
                        } else {
                            console.error("Failed to fetch final outcome after all retries:", outcomeErr);
                            // Don't set error here, let the user see the outcome summary
                        }
                    }
                }

                setIsFetchingOutcome(false);
            } else if (result.roundCompleted && result.nextDilemma) {
                // If the round is completed and there's a next dilemma, update the UI
                console.log("Round completed, moving to next dilemma");
                setDilemma(result.nextDilemma);
                // Reset vote status for the new round
                setHasVoted(false);
                setCurrentRoundNumber(result.nextDilemma.id);
            }

            // Notify callback
            if (onVoteComplete) {
                onVoteComplete(result);
            }

            return result;
        } catch (err) {
            console.error("Failed to submit vote:", err);

            // Check if error is about already voting
            const errorObj = err as { problem?: { detail?: string } };
            if (errorObj?.problem?.detail?.includes("already voted")) {
                setHasVoted(true);
                setError("You have already voted in this round.");
            } else {
                setError("Failed to submit vote. Please try again.");
            }
            throw err;
        } finally {
            setIsSubmitting(false);
        }
    }, [gameId, playerId, token, onVoteComplete, hasVoted]);

    // Fetch dilemma on mount
    useEffect(() => {
        fetchDilemma();
    }, [fetchDilemma]);

    return {
        dilemma,
        isLoading,
        isSubmitting,
        error,
        submitChoice,
        refetch: fetchDilemma,
        isGameOver,
        finalOutcome,
        hasVoted,
        isFetchingOutcome,
    };
}
