import { fetchJson } from "@/shared/http/fetchJson";
import type { ThemeDto } from "./types";

type ThemesResponse = {
    status: "success" | "error";
    data?: unknown;
    message?: string;
};

function normalizeTheme(input: unknown): ThemeDto | null {
    if (!input || typeof input !== "object") return null;
    const obj = input as Record<string, unknown>;

    const id = obj["id"] ?? obj["themeId"];
    const themeName = obj["themeName"] ?? obj["theme"];

    if (typeof id !== "string" || typeof themeName !== "string") return null;
    return { id, themeName };
}

export async function getThemes(token: string): Promise<ThemeDto[]> {
    const res = await fetchJson<ThemesResponse>("/api/themes", {
        method: "GET",
        headers: {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
        },
        cache: "no-store",
    });

    const raw = Array.isArray(res.data) ? res.data : [];
    return raw
        .map((x) => normalizeTheme(x))
        .filter((x): x is ThemeDto => x !== null);
}
