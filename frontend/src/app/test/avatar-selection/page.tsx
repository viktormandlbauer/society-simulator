"use client";

import Link from "next/link";
import { useState } from "react";
import { NesButton } from "@/components/ui/NesButton";
import { AvatarPreview } from "@/components/ui/AvatarPreview";
import { AVATARS } from "@/lib/avatars";

export default function AvatarSelectionTestPage() {
    const [selectedIndex, setSelectedIndex] = useState(0);
    const currentAvatar = AVATARS[selectedIndex];

    const handleNextAvatar = () => {
        setSelectedIndex((prevIndex) => (prevIndex + 1) % AVATARS.length);
    };

    const handlePreviousAvatar = () => {
        setSelectedIndex((prevIndex) => (prevIndex - 1 + AVATARS.length) % AVATARS.length);
    };

    return (
        <div className="nes-container with-title is-rounded is-dark flex flex-col items-center gap-6">
            <p className="title">Avatar Selection Test</p>
            <p className="text-xs text-slate-300 text-center">
                Cycle through available avatars using the buttons.
            </p>

            <div className="flex items-center gap-4">
                <NesButton type="button" onClick={handlePreviousAvatar} aria-label="Previous Avatar">
                    «
                </NesButton>

                <AvatarPreview id={currentAvatar.id} />

                <NesButton type="button" onClick={handleNextAvatar} aria-label="Next Avatar">
                    »
                </NesButton>
            </div>

            <div className="text-xs text-slate-200">
                Selected: {currentAvatar.label} ({selectedIndex + 1}/{AVATARS.length})
            </div>

            <div className="flex gap-3">
                <Link href="/test" className="nes-btn">
                    Back to /test
                </Link>
                <Link href="/" className="nes-btn">
                    Home
                </Link>
            </div>
        </div>
    );
}
