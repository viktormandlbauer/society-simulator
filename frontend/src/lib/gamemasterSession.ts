let gamemasterId: string | null = null;

export function setGamemasterId(id: string) {
    gamemasterId = id;
}

export function getGamemasterId(): string | null {
    return gamemasterId;
}

export function clearGamemasterId() {
    gamemasterId = null;
}
