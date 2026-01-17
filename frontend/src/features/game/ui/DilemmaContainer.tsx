"use client";

import { useState } from "react";
import { DilemmaChoices } from "@/shared/ui/DilemmaChoices";
import { OutcomeSummary } from "@/shared/ui/OutcomeSummary";
import { FinalOutcome } from "@/features/game/ui/FinalOutcome";
import { useDilemma } from "@/features/game/hooks/useDilemma";
import type { VoteResult } from "@/features/game/api/types";

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

    const handleVoteComplete = (result: VoteResult) => {
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
    };

    const { dilemma, isLoading, isSubmitting, error, submitChoice, refetch, finalOutcome, isGameOver, hasVoted, isFetchingOutcome } = useDilemma({
        gameId,
        playerId,
        onVoteComplete: handleVoteComplete,
    });

    console.log("DilemmaContainer state:", { isGameOver, finalOutcome, outcomeSummary, error, isLoading, hasVoted, isFetchingOutcome });

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
