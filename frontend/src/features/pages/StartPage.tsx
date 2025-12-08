"use client";

import {FormEvent, useState} from "react";
import {NesButton} from "@/components/ui/NesButton";
import {NesInput} from "@/components/ui/NesInput";
import {AVATARS} from "@/lib/avatars";
import {AvatarPreview} from "@/components/ui/AvatarPreview";

export function StartPage() {
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [name, setName] = useState("");

    const currentAvatar = AVATARS[selectedIndex];

    function handleNextAvatar() {
        console.log(selectedIndex);
        setSelectedIndex((prevIndex) => (prevIndex + 1) % AVATARS.length);
    }

    function handlePreviousAvatar() {
        console.log(selectedIndex);
        setSelectedIndex((prevIndex) => (prevIndex - 1 + AVATARS.length) % AVATARS.length);
    }

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const trimmedName = name.trim();
        console.log("Name: " + trimmedName, "Avatar Label: " + currentAvatar.label);

        if (!trimmedName) {
            // TODO: show validation error
            return;
        }

        /**
         * TODO: call POST /api/session/guest with:
         * { name: trimmedName, avatarId: currentAvatar.id}
         * and then store PlayerSession + switch to Lobby List (RootShell)
         */
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
                    <NesButton type="button" onClick={handlePreviousAvatar} aria-label="Previous Avatar">
                        «
                    </NesButton>

                    <AvatarPreview id={currentAvatar.id}/>

                    <NesButton type="button" onClick={handleNextAvatar} aria-label="Next Avatar">
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
                    />
                </div>

                {/* Enter Button */}
                <div className="flex items-center gap-4">
                    <NesButton variant="success" type="submit">
                        Enter
                    </NesButton>
                </div>
            </form>
        </div>
    );
}