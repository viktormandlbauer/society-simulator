export type Choice = {
    id: number;
    title: string;
    description: string;
};

export type Dilemma = {
    id: number;
    title: string;
    context: string;
    choices: Choice[];
};

export type VoteRequest = {
    playerId: string;
    choiceId: number;
};

export type VoteResult = {
    roundNumber: number;
    accepted: boolean;
    roundCompleted: boolean;
    counts: Record<number, number>;
    nextDilemma: Dilemma | null;
    outcomeSummary: string | null;
};

export type ApiResponse<T> = {
    status: string;
    data: T;
};
