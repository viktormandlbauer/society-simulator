# Game Feature

This feature handles the game functionality, including dilemmas and voting.

## Structure

- `api/` - API client functions for game endpoints
  - `types.ts` - TypeScript types for game-related data
  - `games.ts` - API functions for interacting with the backend
- `hooks/` - Custom React hooks
  - `useDilemma.ts` - Hook for managing dilemma state and voting
- `ui/` - UI components
  - `DilemmaContainer.tsx` - Container component that connects DilemmaChoices to the backend

## Backend Integration

The game feature connects to the following backend endpoints:

- `GET /api/games/{gameId}/dilemma` - Fetch the current dilemma
- `POST /api/games/{gameId}/dilemma/vote` - Submit a vote
- `POST /api/games/{gameId}/rounds/new` - Trigger a new round
- `POST /api/games` - Create a new game
- `POST /api/games/{gameId}/join` - Join a game
- `POST /api/games/{gameId}/start` - Start a game
- `GET /api/games/{gameId}/intro` - Get game intro

## Usage

### Using the DilemmaContainer Component

```tsx
import { DilemmaContainer } from "@/features/game/ui/DilemmaContainer";

function GamePage() {
  const gameId = "your-game-id";
  const playerId = "your-player-id";

  return (
    <DilemmaContainer
      gameId={gameId}
      playerId={playerId}
      onVoteComplete={(result) => {
        console.log("Vote result:", result);
        // Handle vote completion
      }}
    />
  );
}
```

### Using the useDilemma Hook

```tsx
import { useDilemma } from "@/features/game/hooks/useDilemma";

function CustomDilemmaComponent() {
  const { dilemma, isLoading, isSubmitting, error, submitChoice, refetch } = useDilemma({
    gameId: "your-game-id",
    playerId: "your-player-id",
    onVoteComplete: (result) => {
      console.log("Vote completed:", result);
    },
  });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>{dilemma?.title}</h2>
      <p>{dilemma?.context}</p>
      {/* Render choices and handle voting */}
    </div>
  );
}
```

### Using the API Functions Directly

```tsx
import { getCurrentDilemma, submitVote } from "@/features/game/api/games";

async function fetchDilemma(token: string, gameId: string) {
  const dilemma = await getCurrentDilemma(token, gameId);
  console.log(dilemma);
}

async function vote(token: string, gameId: string, playerId: string, choiceId: number) {
  const result = await submitVote(token, gameId, { playerId, choiceId });
  console.log(result);
}
```

## Testing

A test page is available at `/test/dilemmaChoices` that allows you to:
- Toggle between dummy data and backend integration
- Enter custom game ID and player ID
- Test the voting functionality

## Types

### Dilemma
```typescript
type Dilemma = {
  id: number;
  title: string;
  context: string;
  choices: Choice[];
};
```

### Choice
```typescript
type Choice = {
  id: number;
  title: string;
  description: string;
};
```

### VoteRequest
```typescript
type VoteRequest = {
  playerId: string;
  choiceId: number;
};
```

### VoteResult
```typescript
type VoteResult = {
  roundNumber: number;
  accepted: boolean;
  roundCompleted: boolean;
  counts: Record<number, number>;
  nextDilemma: Dilemma | null;
};
```
