"use client";

import {FormEvent, useState} from "react";
import {NesButton} from "@/components/ui/NesButton";
import {NesInput} from "@/components/ui/NesInput";
import {AVATARS} from "@/lib/avatars";
import {AvatarPreview} from "@/components/ui/AvatarPreview";
import {usePlayerSession} from "@/features/player/PlayerSessionContext";
import {createGuestSession} from "@/lib/api/session";
import {ProblemDetails} from "@/lib/api/problemDetails";

export function StartPage() {
    const {setSession} = usePlayerSession();

    const [selectedIndex, setSelectedIndex] = useState(0);
    const [name, setName] = useState("");

    const [nameError, setNameError] = useState<string | null>(null);
    const [apiError, setApiError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const currentAvatar = AVATARS[selectedIndex];

    function handleNextAvatar() {
        setSelectedIndex((prevIndex) => (prevIndex + 1) % AVATARS.length);
    }

    function handlePreviousAvatar() {
        setSelectedIndex((prevIndex) => (prevIndex - 1 + AVATARS.length) % AVATARS.length);
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        // Reset UI errors on each submit attempt
        setNameError(null);
        setApiError(null);

        // client side normalization/validation
        const trimmedName = name.trim();
        if (!trimmedName) {
            setNameError("Please enter a valid name");
            return;
        }

        setIsSubmitting(true);

        try {
            // call the next.js proxy endpoint
            const res = await createGuestSession({
                name: trimmedName,
                avatarId: currentAvatar.id,
            });

            // Persist session in a single global source of truth
            // TODO: role is currently not returned by the backend, we set it explicitly
            setSession({
                playerId: res.playerId,
                name: res.name,
                avatarId: res.avatarId,
                token: res.token,
                expiresAt: res.expiresAt,
                role: "GUEST",
            });

            console.log("Guest session created: ", res);
        } catch (err) {
            const problem = err as ProblemDetails;

            const message =
                problem.errors?.name ??
                problem.detail ??
                problem.title ??
                "Something went wrong. Please try again later";

            setApiError(message);
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="nes-container with-title is-rounded is-dark flex flex-col items-center">
            <p className="title">Society Simulator</p>
            <p>Welcome to Society Simulator!</p>

            <form
                onSubmit={handleSubmit}
                className="mt-6 flex flex-col gap-6 items-center w-full max-w-md"
            >
                {/* Avatar Selection */}
                <div className="flex items-center gap-4">
                    <NesButton
                        type="button"
                        onClick={handlePreviousAvatar}
                        aria-label="Previous Avatar"
                        disabled={isSubmitting}
                    >
                        «
                    </NesButton>

                    <AvatarPreview id={currentAvatar.id}/>

                    <NesButton
                        type="button"
                        onClick={handleNextAvatar}
                        aria-label="Next Avatar"
                        disabled={isSubmitting}
                    >
                        »
                    </NesButton>
                </div>

                {/* Name Input */}
                <div className="flex items-center gap-4 w-full">
                    <label htmlFor="player-name" className="sr-only">
                        Player name
                    </label>

                    <NesInput
                        id="player-name"
                        placeholder="Enter your name..."
                        className="w-full is-dark"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        autoComplete="off"
                        disabled={isSubmitting}
                    />
                </div>

                {/* Validation / API error feedback */}
                {(nameError || apiError) && (
                    <p className="nes-text is-error text-xs">
                        {nameError ?? apiError}
                    </p>
                )}

                {/* Enter Button */}
                <div className="flex items-center gap-4">
                    <NesButton variant="success" type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "..." : "Enter"}
                    </NesButton>
                </div>
            </form>
        </div>
    );
}