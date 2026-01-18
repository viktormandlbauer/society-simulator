export type ChatMessageDto = {
    playerId: string | null;
    playerName: string;
    avatarId: string | null;
    message: string;
    timestamp: string;
};

export type ChatMessageRequestDto = {
    message: string;
};
