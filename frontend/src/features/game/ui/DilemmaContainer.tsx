"use client";

import { useState, useEffect } from "react";
import { DilemmaChoices } from "@/shared/ui/DilemmaChoices";
import { OutcomeSummary } from "@/shared/ui/OutcomeSummary";
import { FinalOutcome } from "@/features/game/ui/FinalOutcome";
import { useDilemma } from "@/features/game/hooks/useDilemma";
import type { VoteResult } from "@/features/game/api/types";
import { useLobbyChat } from "@/features/lobby/hooks/useLobbyChat";
import { useSessionStore } from "@/features/session/sessionStore";

interface DilemmaContainerProps {
    gameId: string;
    playerId: string;
    onVoteComplete?: (result: VoteResult) => void;
    onGameOver?: () => void;
}

/**
 * Container component that connects DilemmaChoices to the backend.
 * Handles fetching the current dilemma and submitting votes.
 */
export function DilemmaContainer({ gameId, playerId, onVoteComplete, onGameOver }: DilemmaContainerProps) {
    const [outcomeSummary, setOutcomeSummary] = useState<{ summary: string; roundNumber: number } | null>(null);
    const session = useSessionStore((s) => s.session);

    // Initialize the dilemma hook first so updateDilemma is available
    const { dilemma, isLoading, isSubmitting, error, submitChoice, refetch, finalOutcome, isGameOver, hasVoted, isFetchingOutcome, updateDilemma, triggerGameOver } = useDilemma({
        gameId,
        playerId,
        onVoteComplete: (result) => {
            console.log("handleVoteComplete called with:", result);

            // If round is completed and there's an outcome summary, show it
            // BUT only if there's a next dilemma (not the final round)
            if (result.roundCompleted && result.outcomeSummary && result.nextDilemma) {
                console.log("Showing outcome summary for round", result.roundNumber);
                setOutcomeSummary({
                    summary: result.outcomeSummary,
                    roundNumber: result.roundNumber,
                });
            }

            // Call the parent's onVoteComplete if provided
            if (onVoteComplete) {
                onVoteComplete(result);
            }
        },
    });

    // Initialize WebSocket connection and join game room
    const { joinGameRoom, onVoteCompleted, isConnected } = useLobbyChat(session?.token ?? null, null);

    console.log("WebSocket connection status:", isConnected);

    // Join the game room when component mounts
    useEffect(() => {
        if (gameId && isConnected) {
            console.log("Joining game room:", gameId);
            joinGameRoom(gameId);
        } else if (gameId && !isConnected) {
            console.warn("⚠️ Cannot join game room - WebSocket not connected yet");
        }
    }, [gameId, joinGameRoom, isConnected]);

    // Listen for vote completed events from WebSocket
    useEffect(() => {
        console.log("Setting up vote completed listener");
        onVoteCompleted((voteResult) => {
            console.log("Vote completed event received via WebSocket:", voteResult);
            const result = voteResult as VoteResult;

            // Check if game is over (round completed but no next dilemma)
            if (result.roundCompleted && !result.nextDilemma) {
                console.log("Game over detected from WebSocket event - triggering final outcome fetch");
                triggerGameOver();
            } else if (result.nextDilemma && updateDilemma) {
                // Update the dilemma if there's a next one
                console.log("Updating dilemma from WebSocket event");
                updateDilemma(result.nextDilemma);

                // Show outcome summary if available (for players who didn't vote)
                if (result.roundCompleted && result.outcomeSummary) {
                    console.log("Showing outcome summary for round", result.roundNumber);
                    setOutcomeSummary({
                        summary: result.outcomeSummary,
                        roundNumber: result.roundNumber,
                    });
                }
            }

            // Call the parent's onVoteComplete if provided
            if (onVoteComplete) {
                onVoteComplete(result);
            }
        });
    }, [onVoteCompleted, updateDilemma, onVoteComplete, triggerGameOver]);

    console.log("DilemmaContainer state:", {
        isGameOver,
        finalOutcome,
        outcomeSummary,
        error,
        isLoading,
        hasVoted,
        isFetchingOutcome,
        dilemmaId: dilemma?.id,
        dilemmaTitle: dilemma?.title
    });

    if (error) {
        return (
            <section className="nes-container with-title is-rounded is-dark">
                <p className="title">Current Dilemma</p>
                <div className="flex flex-col items-center justify-center py-8 gap-4">
                    <p className="text-sm text-red-400">{error}</p>
                    <button
                        onClick={refetch}
                        className="nes-btn is-error"
                    >
                        Retry
                    </button>
                </div>
            </section>
        );
    }

    // Show loading state while fetching final outcome
    if (isFetchingOutcome) {
        return (
            <section className="nes-container with-title is-rounded is-dark">
                <p className="title">Game Complete!</p>
                <div className="flex flex-col items-center justify-center py-8 gap-4">
                    <p className="text-lg font-bold">🎮 Calculating Final Results...</p>
                    <p className="text-sm text-slate-300">Please wait while we compile the game outcome.</p>
                    <div className="animate-pulse text-2xl">⏳</div>
                </div>
            </section>
        );
    }

    // Show final outcome if game is over
    if (isGameOver && finalOutcome) {
        return (
            <FinalOutcome
                outcome={finalOutcome}
                onExit={() => {
                    if (onGameOver) {
                        onGameOver();
                    }
                }}
            />
        );
    }

    // Show outcome summary if available (but not if game is over)
    if (outcomeSummary && !isGameOver) {
        return (
            <OutcomeSummary
                summary={outcomeSummary.summary}
                roundNumber={outcomeSummary.roundNumber}
                onContinue={() => {
                    console.log("Outcome summary dismissed");
                    setOutcomeSummary(null);
                }}
            />
        );
    }

    return (
        <DilemmaChoices
            data={dilemma || undefined}
            onSubmitChoice={submitChoice}
            isLoading={isLoading}
            isSubmitting={isSubmitting}
            hasVoted={hasVoted}
        />
    );
}
