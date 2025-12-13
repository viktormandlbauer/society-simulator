export type ProblemDetails = {
    type?: string;
    title?: string;
    status?: number;
    detail?: string;
    instance?: string;
    errors?: Record<string, string>;
    timestamp?: string;
    path?: string;
    httpMethod?: string;
    error?: string;
};