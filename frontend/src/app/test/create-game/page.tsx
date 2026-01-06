"use client";

import Link from "next/link";
import {CreateGameForm} from "@/components/ui/CreateGameForm";

export default function GamemasterCreateGamePage() {
    return (
        <div className="nes-container with-title is-rounded is-dark max-w-3xl mx-auto">
            <p className="title">Create Game</p>
            <p className="text-xs text-slate-300 mb-4">
                Create a new game by providing username, player count, theme name, max rounds, and max turn time.
            </p>

            <CreateGameForm />

            <div className="mt-4">
                <Link href="/" className="nes-btn">
                    Home
                </Link>
            </div>
        </div>
    );
}
