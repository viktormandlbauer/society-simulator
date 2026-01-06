"use client";

import {useMemo, useState} from "react";
import {NesButton} from "@/components/ui/NesButton";

type VotingChoice = {
    id: number;
    title: string;
    description: string;
};

export type DilemmaData = {
    id: number;
    title: string;
    context: string;
    choices: VotingChoice[];
};

// Dummy JSON-like data to feed the component
export const DUMMY_DILEMMA: DilemmaData = {
    id: 1,
    title: "Night Curfew for Downtown?",
    context:
        "Citizens are debating whether the city should impose a night curfew to reduce disturbances. Cast your vote to shape the outcome.",
    choices: [
        {
            id: 1,
            title: "Strict curfew",
            description: "Shut down venues after 10pm to keep nights quiet."
        },
        {
            id: 2,
            title: "Balanced approach",
            description: "Keep venues open, but enforce noise limits and patrols."
        },
        {
            id: 3,
            title: "No curfew",
            description: "Let nightlife run freely and trust local businesses."
        },
        {
            id: 4,
            title: "No curfew",
            description: "Let nightlife run freely and trust local businesses."
        }
    ],
};

interface DilemmaChoicesProps {
    data?: DilemmaData;
    onSubmitChoice?: (choiceId: number) => void;
}

export function DilemmaChoices({data = DUMMY_DILEMMA, onSubmitChoice}: DilemmaChoicesProps) {
    const [selectedChoice, setSelectedChoice] = useState<number | null>(null);


    const toggleChoice = (choiceId: number) => {
        setSelectedChoice((prev) => (prev === choiceId ? null : choiceId));
    };

    const submitChoice = () => {
        if (selectedChoice === null) return;
        if (onSubmitChoice) {
            onSubmitChoice(selectedChoice);
        } else {
            console.log(`Submitted vote for choice ${selectedChoice}`);
        }
    };

    return (
        <section className="nes-container with-title is-rounded is-dark">
            <p className="title">Current Dilemma</p>

            <div className="mb-4">
                <p className="font-bold text-sm">{data.title}</p>
                <p className="text-xs text-slate-300">{data.context}</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {data.choices.map((choice) => {
                    const isSelected = selectedChoice === choice.id;

                    return (
                        <article
                            key={choice.id}
                            role="button"
                            tabIndex={0}
                            aria-pressed={isSelected}
                            onClick={() => toggleChoice(choice.id)}
                            onKeyDown={(e) => {
                                if (e.key === "Enter" || e.key === " ") {
                                    e.preventDefault();
                                    toggleChoice(choice.id);
                                }
                            }}
                            className={`nes-container is-rounded h-full nes-pointer transition-colors ${
                                isSelected
                                    ? "border-amber-400 border-2 bg-amber-900/20 shadow-[0_0_0_2px_rgba(251,191,36,0.3)]"
                                    : "bg-slate-900/70"
                            }`}
                        >
                            <div className="flex items-start gap-3">
                                <div className="flex-1">
                                    <p className="text-sm font-bold">
                                        {choice.title}
                                    </p>
                                    <p className="text-xs text-slate-300">
                                        {choice.description}
                                    </p>
                                </div>
                            </div>
                        </article>
                    );
                })}
            </div>

            <div className="mt-4 flex justify-end">
                <NesButton
                    type="button"
                    variant={selectedChoice === null ? "disabled" : "success"}
                    disabled={selectedChoice === null}
                    onClick={submitChoice}
                >
                    Vote
                </NesButton>
            </div>
        </section>
    );
}
