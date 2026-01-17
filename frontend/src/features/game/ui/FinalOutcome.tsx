"use client";

import { NesButton } from "@/shared/ui/NesButton";
import type { FinalOutcome } from "@/features/game/api/games";

interface FinalOutcomeProps {
    outcome: FinalOutcome;
    onExit: () => void;
}

/**
 * Component to display the final outcome after all rounds are completed.
 */
export function FinalOutcome({ outcome, onExit }: FinalOutcomeProps) {
    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Game Over - Final Outcome</p>

            <div className="space-y-6">
                {/* Final Summary */}
                <div className="nes-container is-rounded is-dark">
                    <p className="text-lg font-bold mb-2">Final Summary</p>
                    <p className="text-sm">{outcome.finalSummary}</p>
                </div>

                {/* Round Summaries */}
                <div className="nes-container is-rounded is-dark">
                    <p className="text-lg font-bold mb-4">Round History</p>
                    <div className="space-y-4">
                        {outcome.roundSummaries.map((round) => (
                            <div
                                key={round.roundNumber}
                                className="border-2 border-gray-600 p-3 rounded"
                            >
                                <p className="font-bold text-sm mb-2">
                                    Round {round.roundNumber}: {round.dilemmaTitle}
                                </p>
                                <div className="text-xs space-y-1">
                                    <p>
                                        <span className="font-semibold">Winning Choice:</span>{" "}
                                        {round.winningChoiceId !== null
                                            ? `Choice ${round.winningChoiceId}`
                                            : "No votes"}
                                    </p>
                                    <p>
                                        <span className="font-semibold">Vote Distribution:</span>
                                    </p>
                                    <div className="ml-4">
                                        {Object.entries(round.voteCounts).map(([choiceId, count]) => (
                                            <p key={choiceId}>
                                                Choice {choiceId}: {count} vote{count !== 1 ? "s" : ""}
                                            </p>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Total Statistics */}
                {Object.keys(outcome.totalVotesByChoice).length > 0 && (
                    <div className="nes-container is-rounded is-dark">
                        <p className="text-lg font-bold mb-2">Overall Statistics</p>
                        <p className="text-sm mb-2">Total votes across all rounds:</p>
                        <div className="text-xs ml-4">
                            {Object.entries(outcome.totalVotesByChoice).map(([choiceId, count]) => (
                                <p key={choiceId}>
                                    Choice {choiceId}: {count} vote{count !== 1 ? "s" : ""}
                                </p>
                            ))}
                        </div>
                    </div>
                )}

                {/* Exit Button */}
                <div className="flex justify-center mt-6">
                    <NesButton variant="primary" onClick={onExit}>
                        Exit Game
                    </NesButton>
                </div>
            </div>
        </div>
    );
}
