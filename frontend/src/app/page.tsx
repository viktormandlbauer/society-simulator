"use client";
import {NesButton} from "@/components/ui/NesButton";

const dummyLobbies = [
    {id: "alpha", name: "Dilemma Solver", topic: "Ethics", players: 3, capacity: 8, host: "Ava"},
    {id: "beta", name: "City Builders", topic: "Urban Planning", players: 5, capacity: 10, host: "Leo"},
    {id: "gamma", name: "Eco Futures", topic: "Climate Policy", players: 2, capacity: 6, host: "Nia"},
];

export default function Lobby() {
    const handleJoin = (lobbyId: string) => {
        console.log(`Joining lobby ${lobbyId}`);
    };

    const handleCreate = () => {
        console.log("Creating a new lobby");
    };

    return (
        <div className="nes-container with-title is-rounded is-dark space-y-4">
            <p className="title">Join a Lobby!</p>

            <div className="space-y-3">
                {dummyLobbies.map(lobby => (
                    <div
                        key={lobby.id}
                        className="nes-container is-rounded flex items-center justify-between p-4"
                    >
                        <div>
                            <p className="font-bold">{lobby.name}</p>
                            <p className="text-sm text-gray-400">
                                {lobby.topic} · Host: {lobby.host} · {lobby.players}/{lobby.capacity} players
                            </p>
                        </div>
                        <NesButton variant="success" onEnter={() => handleJoin(lobby.id)}>
                            Join
                        </NesButton>
                    </div>
                ))}
            </div>

            <div className="flex justify-end">
                <NesButton variant="normal" onEnter={handleCreate}>
                    Create a Lobby
                </NesButton>
            </div>
        </div>
    );
}
