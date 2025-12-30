export type ProblemDetails = {
    type?: string;
    title?: string;
    status?: number;
    detail?: string;
    instance?: string;
    errors?: Record<string, string[]>;
};

export class ProblemDetailsError extends Error {
    readonly problem: ProblemDetails;

    constructor(problem: ProblemDetails) {
        super(problem.title ?? problem.detail ?? "Request failed");
        this.problem = problem;
        this.name = "ProblemDetailsError";
    }
}
