import {NextResponse} from "next/server";

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

function isJsonLike(contentType: string) {
    // covers application/json, application/problem+json, application/vnd.something+json, etc.
    const ct = contentType.toLowerCase();
    return ct.includes("application/json") || ct.includes("application/problem+json") || ct.includes("+json");
}

/**
 * Proxies a request to the Spring backend and returns a NextResponse.
 * - Forwards Authorization header if present.
 * - Normalizes network errors to ProblemDetails responses.
 * - Forwards JSON responses (including ProblemDetails) with original status codes.
 */
export async function proxyToSpring(request: Request, springPath: string) {
    const baseUrl = process.env.SPRING_API_URL;
    if (!baseUrl) {
        return problem(500, "Server misconfigured", "SPRING_API_URL is not set");
    }

    const url = `${baseUrl}${springPath}`;

    // Forwards the Authorization header if present
    const auth = request.headers.get("authorization") ?? undefined;
    const contentType = request.headers.get("content-type") ?? "application/json";

    // Read body only for non-GET requests
    const method = request.method.toUpperCase();
    const body =
        method === "GET" || method === "HEAD" ? undefined : await request.text();

    // Perform the upstream request
    try {
        const upstream = await fetch(url, {
            method,
            headers: {
                Accept: "application/json",
                "Content-Type": contentType,
                ...(method === "GET" || method === "HEAD" ? {} : { "Content-Type": contentType }),
                ...(auth ? { Authorization: auth } : {}),
            },
            body,
            cache: "no-store",
        });

        const text = await upstream.text();
        if (upstream.ok && (upstream.status === 204 || text === "")) {
            return new NextResponse(null, { status: upstream.status });
        }

        const upstreamContentType = upstream.headers.get("content-type") ?? "";

        // JSON-like responses (incl. ProblemDetails)
        if (isJsonLike(upstreamContentType)) {
            const data = text ? safeJsonParse(text) : null;
            if (data !== null) {
                return NextResponse.json(data, { status: upstream.status });
            }
            return problem(502, "Bad gateway", "Upstream returned invalid JSON.");
        }

        // Non-JSON error:
        if (!upstream.ok) {
            return problem(upstream.status, "Request failed", text || upstream.statusText);
        }

        // Non-JSON success:
        return new NextResponse(text, {
            status: upstream.status,
            headers: upstreamContentType ? { "content-type": upstreamContentType } : undefined,
        });
    } catch {
        return problem(502, "Server unavailable", "Could not reach Spring API.");
    }
}