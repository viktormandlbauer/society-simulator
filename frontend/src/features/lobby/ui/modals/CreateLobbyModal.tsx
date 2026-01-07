"use client";

import {ThemeDto} from "@/features/themes/api/types";
import {getProblemFieldError, getProblemMessage, ProblemDetails} from "@/shared/http/problemDetails";
import {CreateLobbyRequestDto} from "@/features/lobby/api/types";
import {useEffect, useMemo, useState} from "react";
import {NesInput} from "@/shared/ui/NesInput";
import {NesButton} from "@/shared/ui/NesButton";

type CreateLobbyFormState = {
    name: string;
    themeId: string;
    maxPlayers: number;
    maxRounds: number;
    password: string;
};

type Props = {
    open: boolean;
    themes: ThemeDto[];
    isLoading: boolean;
    error: ProblemDetails | null;

    onCancel: () => void;
    onCreate: (req: CreateLobbyRequestDto) => void;
};

export function CreateLobbyModal({ open, themes, isLoading, error, onCancel, onCreate }: Props) {
    const defaultThemeId = useMemo(() => (themes.length > 0 ? themes[0].id : ""), [themes]);

    const [form, setForm] = useState<CreateLobbyFormState>({
        name: "",
        themeId: "",
        maxPlayers: 8,
        maxRounds: 5,
        password: "",
    });

    // When modal opens and themes are available, ensure themeId is initialized.
    useEffect(() => {
        if (!open) return;

        setForm((prev) => {
            if (prev.themeId) return prev;
            if (!defaultThemeId) return prev;
            return { ...prev, themeId: defaultThemeId };
        });
    }, [open, defaultThemeId]);

    // Reset form when closing
    useEffect(() => {
        if (open) return;
        setForm({
            name: "",
            themeId: "",
            maxPlayers: 8,
            maxRounds: 5,
            password: "",
        });
    }, [open]);

    if (!open) return null;

    const nameError = getProblemFieldError(error, "name");
    const themeError = getProblemFieldError(error, "themeId");
    const maxPlayersError = getProblemFieldError(error, "maxPlayers");
    const maxRoundsError = getProblemFieldError(error, "maxRounds");

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
            <div className="nes-container is-rounded is-dark w-full max-w-2xl">
                <p className="mb-4">Create Lobby</p>

                {error && (
                    <div className="nes-container is-rounded is-dark mb-3 border border-red-500/50">
                        <p className="text-sm">{error.title ?? "Error"}</p>
                        <p className="text-xs opacity-80">{getProblemMessage(error)}</p>
                    </div>
                )}

                <div className="flex flex-col gap-3">
                    <label className="flex flex-col gap-2">
                        <span>Lobby name</span>
                        <NesInput
                            value={form.name}
                            onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))}
                            placeholder="e.g. Team A"
                            className="is-dark w-full"
                            disabled={isLoading}
                        />
                        {nameError && <p className="nes-text is-error text-xs">{nameError}</p>}
                    </label>

                    <label className="flex flex-col gap-2">
                        <span>Theme</span>
                        <div className="nes-select is-dark">
                            <select
                                value={form.themeId}
                                onChange={(e) => setForm((p) => ({ ...p, themeId: e.target.value }))}
                                disabled={isLoading}
                            >
                                {themes.map((t) => (
                                    <option key={t.id} value={t.id}>
                                        {t.themeName}
                                    </option>
                                ))}
                            </select>
                        </div>
                        {themeError && <p className="nes-text is-error text-xs">{themeError}</p>}
                    </label>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                        <label className="flex flex-col gap-2">
                            <span>Max players (2–8)</span>
                            <NesInput
                                type="number"
                                min={2}
                                max={8}
                                value={form.maxPlayers}
                                onChange={(e) => setForm((p) => ({ ...p, maxPlayers: Number(e.target.value) }))}
                                className="is-dark w-full"
                                disabled={isLoading}
                            />
                            {maxPlayersError && <p className="nes-text is-error text-xs">{maxPlayersError}</p>}
                        </label>

                        <label className="flex flex-col gap-2">
                            <span>Max rounds (3–10)</span>
                            <NesInput
                                type="number"
                                min={3}
                                max={10}
                                value={form.maxRounds}
                                onChange={(e) => setForm((p) => ({ ...p, maxRounds: Number(e.target.value) }))}
                                className="is-dark w-full"
                                disabled={isLoading}
                            />
                            {maxRoundsError && <p className="nes-text is-error text-xs">{maxRoundsError}</p>}
                        </label>
                    </div>

                    <label className="flex flex-col gap-2">
                        <span>Password (optional)</span>
                        <NesInput
                            type="password"
                            value={form.password}
                            onChange={(e) => setForm((p) => ({ ...p, password: e.target.value }))}
                            placeholder="leave empty for public lobby"
                            className="is-dark w-full"
                            disabled={isLoading}
                        />
                    </label>

                    <div className="flex gap-2 justify-end pt-2">
                        <NesButton variant="warning" disabled={isLoading} onClick={onCancel}>
                            Cancel
                        </NesButton>

                        <NesButton
                            variant="success"
                            disabled={isLoading}
                            onClick={() => {
                                const name = form.name.trim();

                                // Light client-side validation
                                if (name.length < 3) return;

                                onCreate({
                                    name,
                                    themeId: form.themeId,
                                    maxPlayers: form.maxPlayers,
                                    maxRounds: form.maxRounds,
                                    // Normalize optional password
                                    password: form.password.trim() ? form.password.trim() : undefined,
                                });
                            }}
                        >
                            Create
                        </NesButton>
                    </div>
                </div>
            </div>
        </div>
    );
}
