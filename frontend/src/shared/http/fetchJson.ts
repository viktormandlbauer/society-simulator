import type { ProblemDetails } from "./problemDetails";
import { ProblemDetailsError } from "./problemDetails";
import { safeJsonParse } from "./json";

/**
 * Fetch helper that:
 * - reads response body as text
 * - attempts to parse JSON if body is not empty
 * - throws ProblemDetailsError if response is not ok
 */
export async function fetchJson<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
    const res = await fetch(input, init);

    const text = await res.text();
    const data = text ? safeJsonParse(text) : null;

    if (!res.ok) {
        const problem = (data ?? { title: "Request failed", status: res.status }) as ProblemDetails;
        throw new ProblemDetailsError(problem);
    }

    return data as T;
}

/**
 * Same as fetchJson, but for endpoints returning 204 No Content.
 */
export async function fetchNoContent(input: RequestInfo, init?: RequestInit): Promise<void> {
    const res = await fetch(input, init);

    const text = await res.text();
    const data = text ? safeJsonParse(text) : null;

    if (!res.ok) {
        const problem = (data ?? { title: "Request failed", status: res.status }) as ProblemDetails;
        throw new ProblemDetailsError(problem);
    }
}