"use client";

import {FormEvent, useEffect, useMemo, useState} from "react";
import {NesInput} from "@/components/ui/NesInput";
import {NesButton} from "@/components/ui/NesButton";
import {API_BASE_URL} from "@/lib/apiConfig";

type Status = "idle" | "submitting" | "success" | "error";

type CreateGameFormState = {
    username: string;
    playerCount: string;
    themeName: string;
    maxRounds: string;
    maxTurnTime: string;
};

export interface CreateGameFormProps {
    apiBaseUrl?: string;
    apiPath?: string;
    onSuccess?: () => void;
}

const DEFAULT_API_PATH = "/api/games";
const THEMES_PATH = "/api/themes";

export function CreateGameForm({
    apiBaseUrl = API_BASE_URL,
    apiPath = DEFAULT_API_PATH,
    onSuccess,
}: CreateGameFormProps) {
    const [form, setForm] = useState<CreateGameFormState>({
        username: "",
        playerCount: "4",
        themeName: "",
        maxRounds: "5",
        maxTurnTime: "60",
    });
    const [status, setStatus] = useState<Status>("idle");
    const [error, setError] = useState<string | null>(null);
    const [themes, setThemes] = useState<string[]>([]);
    const [themesError, setThemesError] = useState<string | null>(null);
    const [isLoadingThemes, setIsLoadingThemes] = useState(false);

    const isSubmitting = status === "submitting";

    const parsed = useMemo(
        () => ({
            username: form.username.trim(),
            playerCount: parseInt(form.playerCount, 10),
            themeName: form.themeName.trim(),
            maxRounds: parseInt(form.maxRounds, 10),
            maxTurnTime: parseInt(form.maxTurnTime, 10),
        }),
        [form],
    );

    const canSubmit =
        !isSubmitting &&
        parsed.username.length > 0 &&
        parsed.themeName.length > 0 &&
        Number.isFinite(parsed.playerCount) && parsed.playerCount > 0 &&
        Number.isFinite(parsed.maxRounds) && parsed.maxRounds > 0 &&
        Number.isFinite(parsed.maxTurnTime) && parsed.maxTurnTime > 0;

    const handleChange = (key: keyof CreateGameFormState) => (value: string) => {
        setForm((prev) => ({...prev, [key]: value}));
        if (status !== "submitting") {
            setStatus("idle");
            setError(null);
        }
    };

    useEffect(() => {
        const fetchThemes = async () => {
            setIsLoadingThemes(true);
            setThemesError(null);
            try {
                const response = await fetch(`${apiBaseUrl}${THEMES_PATH}`);
                if (!response.ok) {
                    const message = (await response.text()).trim();
                    throw new Error(message || "Failed to load themes.");
                }
                const payload = await response.json().catch(() => null);
                // Accept array of strings or array of objects with "theme"/"name" keys
                const extracted: any[] =
                    Array.isArray(payload)
                        ? payload
                        : Array.isArray(payload?.data)
                            ? payload.data
                            : [];
                const names = extracted
                    .map((t) =>
                        typeof t === "string"
                            ? t
                            : t?.theme ?? t?.name,
                    )
                    .filter((t: any): t is string => Boolean(t));

                setThemes(names);
                if (names.length > 0 && !form.themeName) {
                    setForm((prev) => ({...prev, themeName: names[0]}));
                }
            } catch (err) {
                setThemesError(err instanceof Error ? err.message : "Failed to load themes.");
            } finally {
                setIsLoadingThemes(false);
            }
        };

        fetchThemes();
        // apiBaseUrl is stable unless prop changes
    }, [apiBaseUrl]);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        if (!canSubmit) return;

        setStatus("submitting");
        setError(null);

        try {
            const response = await fetch(`${apiBaseUrl}${apiPath}`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    username: parsed.username,
                    playerCount: parsed.playerCount,
                    themeName: parsed.themeName,
                    maxRounds: parsed.maxRounds,
                    maxTurnTime: parsed.maxTurnTime,
                }),
            });

            if (!response.ok) {
                const message = (await response.text()).trim();
                throw new Error(message || "Game creation failed.");
            }

            setStatus("success");
            if (onSuccess) onSuccess();
        } catch (err) {
            setStatus("error");
            setError(err instanceof Error ? err.message : "Something went wrong.");
        }
    }

    return (
        <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <Field
                label="Username"
                id="username"
                value={form.username}
                onChange={handleChange("username")}
                disabled={isSubmitting}
            />

            <Field
                label="Player Count"
                id="playerCount"
                type="number"
                min={1}
                value={form.playerCount}
                onChange={handleChange("playerCount")}
                disabled={isSubmitting}
            />

            <ThemeSelect
                label="Theme"
                id="themeName"
                value={form.themeName}
                options={themes}
                loading={isLoadingThemes}
                disabled={isSubmitting || isLoadingThemes}
                onChange={handleChange("themeName")}
            />

            <Field
                label="Max Rounds"
                id="maxRounds"
                type="number"
                min={1}
                value={form.maxRounds}
                onChange={handleChange("maxRounds")}
                disabled={isSubmitting}
            />

            <Field
                label="Max Turn Time (seconds)"
                id="maxTurnTime"
                type="number"
                min={1}
                value={form.maxTurnTime}
                onChange={handleChange("maxTurnTime")}
                disabled={isSubmitting}
            />

            {error && (
                <p className="text-xs text-red-400" role="alert">
                    {error}
                </p>
            )}
            {themesError && (
                <p className="text-xs text-red-400" role="alert">
                    {themesError}
                </p>
            )}
            {status === "success" && (
                <p className="text-xs text-green-400" role="status">
                    Game created successfully.
                </p>
            )}

            <div className="flex gap-3 items-center">
                <NesButton type="submit" variant={canSubmit ? "success" : "disabled"} disabled={!canSubmit}>
                    {isSubmitting ? "Submitting..." : "Create Game"}
                </NesButton>
            </div>
        </form>
    );
}

interface FieldProps {
    label: string;
    id: string;
    value: string;
    onChange: (value: string) => void;
    disabled?: boolean;
    type?: string;
    min?: number;
}

function Field({label, id, value, onChange, disabled, type = "text", min}: FieldProps) {
    return (
        <div className="flex flex-col gap-2">
            <label className="text-xs" htmlFor={id}>
                {label}
            </label>
            <NesInput
                id={id}
                name={id}
                type={type}
                min={min}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className="is-dark"
                disabled={disabled}
            />
        </div>
    );
}

interface ThemeSelectProps {
    label: string;
    id: string;
    value: string;
    options: string[];
    onChange: (value: string) => void;
    loading?: boolean;
    disabled?: boolean;
}

function ThemeSelect({label, id, value, options, onChange, loading = false, disabled = false}: ThemeSelectProps) {
    return (
        <div className="flex flex-col gap-2">
            <label className="text-xs" htmlFor={id}>
                {label} {loading ? "(loading...)" : ""}
            </label>
            <div className="nes-select is-dark">
                <select
                    id={id}
                    name={id}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    disabled={disabled || options.length === 0}
                >
                    {options.length === 0 && <option value="">No themes available</option>}
                    {options.map((theme) => (
                        <option key={theme} value={theme}>
                            {theme}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
}
