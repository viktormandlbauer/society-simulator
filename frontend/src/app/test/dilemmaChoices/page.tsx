"use client";

import {DilemmaChoices, DUMMY_DILEMMA} from "@/shared/ui/DilemmaChoices";
import {DilemmaContainer} from "@/features/game/ui/DilemmaContainer";
import {useState} from "react";

export default function DilemmaChoicesTestPage() {
    const [useBackend, setUseBackend] = useState(false);
    const [gameId, setGameId] = useState("00000000-0000-0000-0000-000000000000");
    const [playerId, setPlayerId] = useState("00000000-0000-0000-0000-000000000001");

    return (
        <div className="container mx-auto p-4 space-y-4">
            <div className="nes-container is-rounded is-dark">
                <h2 className="text-lg font-bold mb-4">Dilemma Choices Test Page</h2>

                <div className="mb-4">
                    <label className="flex items-center gap-2 cursor-pointer">
                        <input
                            type="checkbox"
                            className="nes-checkbox"
                            checked={useBackend}
                            onChange={(e) => setUseBackend(e.target.checked)}
                        />
                        <span>Use Backend Integration</span>
                    </label>
                </div>

                {useBackend && (
                    <div className="space-y-2">
                        <div className="nes-field">
                            <label htmlFor="gameId">Game ID:</label>
                            <input
                                type="text"
                                id="gameId"
                                className="nes-input is-dark"
                                value={gameId}
                                onChange={(e) => setGameId(e.target.value)}
                            />
                        </div>
                        <div className="nes-field">
                            <label htmlFor="playerId">Player ID:</label>
                            <input
                                type="text"
                                id="playerId"
                                className="nes-input is-dark"
                                value={playerId}
                                onChange={(e) => setPlayerId(e.target.value)}
                            />
                        </div>
                    </div>
                )}
            </div>

            {useBackend ? (
                <DilemmaContainer
                    gameId={gameId}
                    playerId={playerId}
                    onVoteComplete={(result) => {
                        console.log("Vote result:", result);
                        alert(`Vote submitted! Round ${result.roundNumber} - ${result.accepted ? "Accepted" : "Rejected"}`);
                    }}
                />
            ) : (
                <DilemmaChoices data={DUMMY_DILEMMA} />
            )}
        </div>
    );
}
