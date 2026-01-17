// This type represents the Problem Details for HTTP APIs as per RFC 7807
export type ProblemDetails = {
    type?: string;
    title?: string;
    status?: number;
    detail?: string;
    instance?: string;
    errors?: Record<string, string | string[]>;
};

// Maps specific problem detail types to user-friendly messages to show in the UI
const USER_FRIENDLY_BY_TYPE: Record<string, string> = {
    "https://example.com/probs/lobby-password-invalid": "Wrong password. Please try again.",
    "https://example.com/probs/lobby-full": "This lobby is full.",
    "https://example.com/probs/lobby-not-joinable": "You can’t join this lobby right now.",
    "https://example.com/probs/player-already-in-lobby": "You’re already in another lobby. Leave it first.",
    "https://example.com/probs/lobby-not-found": "This lobby no longer exists.",
    "https://example.com/probs/theme-not-found": "Selected theme doesn’t exist anymore.",
    "https://example.com/probs/validation-failed": "Please check the highlighted fields.",
    "https://example.com/probs/malformed-json": "Invalid input. Please try again.",
};

// Returns a user-friendly message for a ProblemDetails response.
export function getProblemMessage(problem: ProblemDetails): string {
    const mapped = problem.type ? USER_FRIENDLY_BY_TYPE[problem.type] : undefined;
    return mapped ?? problem.detail ?? problem.title ?? "Something went wrong. Please try again later";
}

// Returns a field-level error message from ProblemDetails.
export function getProblemFieldError(problem: ProblemDetails | null, field: string): string | null {
    const raw = problem?.errors?.[field];
    if (!raw) return null;
    return Array.isArray(raw) ? raw.join(", ") : raw;
}

export class ProblemDetailsError extends Error {
    readonly problem: ProblemDetails;

    constructor(problem: ProblemDetails) {
        super(getProblemMessage(problem));
        this.problem = problem;
        this.name = "ProblemDetailsError";

        // Restore prototype chain for instanceof checks
        Object.setPrototypeOf(this, ProblemDetailsError.prototype);
    }
}

/**
 * Helper for catch-blocks:
 * - If `fetchJson` throws a ProblemDetailsError, unwrap it.
 * - If a plain object shaped like ProblemDetails is thrown, accept it.
 */
export function asProblemDetails(e: unknown): ProblemDetails | null {
    if (e instanceof ProblemDetailsError) return e.problem;

    if (e && typeof e === "object") {
        const obj = e as Record<string, unknown>;
        if (
            typeof obj["type"] === "string" ||
            typeof obj["title"] === "string" ||
            typeof obj["detail"] === "string"
        ) {
            return obj as ProblemDetails;
        }
    }

    return null;
}