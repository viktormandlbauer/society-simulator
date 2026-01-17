  "use client";

interface OutcomeSummaryProps {
    summary: string;
    roundNumber: number;
    onContinue?: () => void;
}

/**
 * Component to display the outcome summary after a round is completed.
 * Shows the AI-generated narrative summary of what happened based on the voting results.
 */
export function OutcomeSummary({ summary, roundNumber, onContinue }: OutcomeSummaryProps) {
    return (
        <section className="nes-container with-title is-rounded is-dark">
            <p className="title">Round {roundNumber} - Outcome</p>

            <div className="mb-4">
                <p className="text-sm text-slate-200 leading-relaxed">
                    {summary}
                </p>
            </div>

            {onContinue && (
                <div className="flex justify-end">
                    <button
                        onClick={onContinue}
                        className="nes-btn is-primary"
                    >
                        Continue
                    </button>
                </div>
            )}
        </section>
    );
}
