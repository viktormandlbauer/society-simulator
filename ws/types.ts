export type ServerToClientEvents = {
  "lobby:presence": (p: { id: string; name?: string; type: "join" | "leave" }) => void;
  "lobby:voteUpdate": (p: { userId: string; choice: string; ts: number }) => void;
  "game:update": (p: { action: unknown; by: string; ts: number }) => void;
  
  // For testing purpose
  "test:pong": (p: { msg: string; at: number }) => void;
  "test:chat": (p: { text: string; from: string; at: number }) => void;
};

export type ClientToServerEvents = {
  "lobby:join": (p: { lobbyId: string; name?: string }) => void;
  "lobby:leave": (p: { lobbyId: string }) => void;
  "lobby:vote": (p: { lobbyId: string; choice: string }) => void;
  "game:action": (p: { lobbyId: string; action: unknown }) => void;

  // For testing purpose
  "test:ping": (p: { msg: string }) => void;
  "test:chat": (p: { text: string }) => void;
};

export type InterServerEvents = Record<string, never>;

export type SocketData = {
  name?: string;
  lobbyId?: string;
};
