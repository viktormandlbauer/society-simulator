"use client";

import {getProblemMessage, ProblemDetails} from "@/shared/http/problemDetails";
import {useEffect, useState} from "react";
import {NesInput} from "@/shared/ui/NesInput";
import {NesButton} from "@/shared/ui/NesButton";

type Props = {
    open: boolean;
    lobbyName: string;

    isLoading: boolean;
    error: ProblemDetails | null;

    onCancel: () => void;
    onJoin: (password: string) => void;
};

export function JoinLobbyModal({open, lobbyName, isLoading, error, onCancel, onJoin}: Props) {
    const [password, setPassword] = useState("");

    // Reset input whenever the modal is opened/closed
    useEffect(() => {
        if (!open) setPassword("");
    }, [open]);

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
            <div className="nes-container is-rounded is-dark w-full max-w-lg">
                <p className="mb-3">Password required for: {lobbyName}</p>

                {error && (
                    <div className="nes-container is-rounded is-dark mb-3 border border-red-500/50">
                        <p className="text-sm">{error.title ?? "Error"}</p>
                        <p className="text-xs opacity-80">{getProblemMessage(error)}</p>
                    </div>
                )}

                <NesInput
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Lobby password"
                    className="is-dark w-full"
                    disabled={isLoading}
                />

                <div className="flex gap-2 justify-end mt-4">
                    <NesButton variant="warning" disabled={isLoading} onClick={onCancel}>
                        Cancel
                    </NesButton>

                    <NesButton
                        variant="success"
                        disabled={isLoading}
                        onClick={() => onJoin(password)}
                    >
                        Join
                    </NesButton>
                </div>
            </div>
        </div>
    );
}