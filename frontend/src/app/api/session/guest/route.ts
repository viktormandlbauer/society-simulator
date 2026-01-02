import { NextResponse } from "next/server";

type ProblemDetails = {
    type?: string;
    title?: string;
    status?: number;
    detail?: string;
    instance?: string;
    errors?: Record<string, string[]>;
};

function problem(status: number, title: string, detail?: string) {
    const body: ProblemDetails = { status, title, detail };
    return NextResponse.json(body, { status });
}

function safeJsonParse(text: string): unknown | null {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}

export async function POST(request: Request) {
    const baseUrl = process.env.SPRING_API_URL;
    if (!baseUrl) {
        return problem(500, "Server misconfigured", "SPRING_API_URL is not set");
    }

    const body = await request.text();

    try {
        const upstream = await fetch(`${baseUrl}/api/session/guest`, {
            method: "POST",
            headers: {
                "Content-Type": request.headers.get("content-type") ?? "application/json",
                Accept: "application/json",
            },
            body,
            cache: "no-store",
        });

        // Prefer reading text once; then decide how to respond.
        const text = await upstream.text();
        const contentType = upstream.headers.get("content-type") ?? "";
        const isJson = contentType.includes("application/json");

        if (isJson) {
            const data = text ? safeJsonParse(text) : null;

            if (data) {
                // Forward JSON (success DTO or ProblemDetails) with upstream status.
                return NextResponse.json(data, { status: upstream.status });
            }

            // Upstream claimed JSON but returned empty/invalid JSON.
            return problem(
                502,
                "Bad gateway",
                "Upstream returned invalid JSON."
            );
        }

        // Non-JSON from upstream. If it's an error, wrap it as ProblemDetails.
        if (!upstream.ok) {
            return problem(
                upstream.status,
                "Request failed",
                text || upstream.statusText
            );
        }

        // If upstream ok but non-JSON, that's unexpected in our API contract.
        return problem(
            502,
            "Bad gateway",
            "Upstream returned non-JSON response."
        );
    } catch (e) {
        // Network failure: Spring down, connection refused, DNS issues, etc.
        return problem(
            502,
            "Backend unavailable",
            "Server might be down. Please try again later."
        );
    }
}
