"use client";

import {NesButton} from "@/components/ui/NesButton";
import {NesInput} from "@/components/ui/NesInput";
import {useState} from "react";
import {AVATARS} from "@/lib/avatars";
import {AvatarPreview} from "@/components/ui/AvatarPreview";

export default function Home() {
    const [selectedIndex, setSelectedIndex] = useState(0);
    const currentAvatar = AVATARS[selectedIndex];

    const handleEnter = () => {
        // TODO: Implement enter button functionality
    }

    function handleNextAvatar() {
        console.log(selectedIndex);
        setSelectedIndex((prevIndex) => (prevIndex + 1) % AVATARS.length);
    }

    function handlePreviousAvatar() {
        console.log(selectedIndex);
        setSelectedIndex((prevIndex) => (prevIndex - 1 + AVATARS.length) % AVATARS.length);
    }

    return (
        <div className="nes-container with-title is-rounded is-dark flex flex-col items-center">
            <p className="title">Society Simulator</p>
            <p>Welcome to Society Simulator!</p>

            <div className="mt-6 flex flex-col gap-6 items-center">
                {/* Avatar Selection */}
                <div className="flex items-center gap-4">
                    <NesButton type="button" onClick={handlePreviousAvatar} aria-label="Previous Avatar">
                        «
                    </NesButton>

                    <AvatarPreview id={currentAvatar.id} />

                    <NesButton type="button" onClick={handleNextAvatar} aria-label="Next Avatar">
                        »
                    </NesButton>
                </div>

                {/* Enter + Name Input */}
                <div className="flex items-center gap-4">
                    <label htmlFor="player-name" className="sr-only">
                        Player name
                    </label>
                    <NesInput
                        placeholder="Enter your name..."
                        className="w-full is-dark"
                    />
                </div>
                <div className="flex items-center gap-4">
                    <NesButton variant="success" type="button">
                        Enter
                    </NesButton>
                </div>
            </div>
        </div>
    );

}