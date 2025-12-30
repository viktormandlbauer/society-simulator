"use client";

import {FormEvent, useState} from "react";
import {NesButton} from "@/shared/ui/NesButton";
import {NesInput} from "@/shared/ui/NesInput";
import {AVATARS} from "@/shared/avatars";
import {AvatarPreview} from "@/shared/ui/AvatarPreview";
import {createGuestSession} from "@/features/session/api/session";
import {ProblemDetailsError} from "@/shared/http/problemDetails";
import {useSessionStore} from "@/features/session/sessionStore";

export function StartPage() {
    const setSession = useSessionStore((s) => s.setSession);

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

            const newSession = {
                playerId: res.playerId,
                name: res.name,
                avatarId: res.avatarId,
                token: res.token,
                expiresAt: res.expiresAt,
                role: res.role,
            };

            setSession(newSession);

            console.log("Guest session created: ", res);
        } catch (err) {
            if (err instanceof ProblemDetailsError) {
                const problem = err.problem;

                // Log only server-side/infrastructure failures (5xx)
                if (!problem.status || problem.status >= 500) {
                    console.error("Session request failed:", problem);
                }

                // `errors.name`ist string[] (first message is enough for UI)
                const fieldMsg = problem.errors?.name?.[0];

                setApiError(
                    fieldMsg ??
                    problem.detail ??
                    problem.title ??
                    "Something went wrong. Please try again later."
                );
            } else {
                // Unexpected Error
                setApiError("Something went wrong. Please try again later.");
                console.error(err);
            }
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