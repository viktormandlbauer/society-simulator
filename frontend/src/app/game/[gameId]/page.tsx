"use client";

import { useParams } from "next/navigation";
import { useSessionStore } from "@/features/session/sessionStore";
import { DilemmaContainer } from "@/features/game/ui/DilemmaContainer";
import { NesButton } from "@/shared/ui/NesButton";
import { useRouter } from "next/navigation";

export default function GamePage() {
    const params = useParams();
    const router = useRouter();
    const session = useSessionStore((s) => s.session);
    const gameId = params.gameId as string;

    if (!session) {
        return (
            <div className="container mx-auto p-4">
                <div className="nes-container with-title is-rounded is-dark">
                    <p className="title">Game</p>
                    <p className="text-sm">You must be logged in to view this game.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4 space-y-4">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold">Game Session</h1>
                <NesButton
                    variant="warning"
                    onClick={() => router.push("/")}
                >
                    Leave Game
                </NesButton>
            </div>

            <DilemmaContainer
                gameId={gameId}
                playerId={session.playerId}
                onVoteComplete={(result) => {
                    console.log("Vote result:", result);
                    // You can add additional UI feedback here
                    if (result.roundCompleted) {
                        console.log("Round completed! Vote counts:", result.counts);
                    }
                }}
                onGameOver={() => {
                    console.log("Game over! Redirecting to home...");
                    // Optionally redirect after a delay
                    setTimeout(() => {
                        router.push("/");
                    }, 3000);
                }}
            />

            {/* Placeholder for additional game UI */}
            <div className="nes-container is-rounded is-dark">
                <p className="title">Game Info</p>
                <div className="text-sm space-y-2">
                    <p><span className="font-bold">Game ID:</span> {gameId}</p>
                    <p><span className="font-bold">Player:</span> {session.name}</p>
                </div>
            </div>
        </div>
    );
}
