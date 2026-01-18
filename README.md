# Society Simulator

Society Simulator is a text-based, AI-driven game where players are presented with thought-provoking dilemmas centered around societal issues. Play solo or with a group, discuss the scenarios, and vote on decisions together in real time.

Each collective choice shapes the direction of the story, leading to new AI-generated scenarios and outcomes influenced by the group’s decisions.

Society Simulator can be used as an educational tool to encourage critical thinking and discussion in classrooms, or as a fun and engaging game for game nights with friends and family.

Built with Spring, React / Next.js, WebSocket, styled with NES.css, and powered by an AI LLaMA model via DeepInfra.

## How to Play

1.  Choose an avatar and enter your name.
    
2.  Join an existing lobby or create a new one.
    
3.  If you create a lobby, you become the **Game Master**.
    *   Name the lobby
        
    *   Select a topic (e.g., hospital, zombie apocalypse, politics)
        
4.   Set the number of players and rounds.
    
5.   Create the lobby. Other players can join, or you may play solo as the Game Master.
    
6.   Start the game.
    
7.   The AI presents a dilemma-based scenario.
    
8.   Several possible choices are shown.
    
9.   Players vote on their preferred option in real time.
    
10.   The option with the most votes is selected.
    
11.   The game continues with a new AI-generated scenario each round.
    
12.   After the final round, the game reveals the overall outcome and displays a recap of all decisions.
 

### Notes

*   The creator of a lobby is the **Game Master**.
    
*   If the Game Master leaves the lobby, the role is automatically passed to the next player.
    
*   If only one player remains in a lobby and leaves, the lobby is deleted.


## Tech Stack

### Backend

- Java
- Spring (Spring Boot)
- WebSocket (real-time communication)

### Frontend

- React
- Next.js
- NES.css (https://nostalgic-css.github.io/NES.css/)

### AI/KI

- LLaMA language model
- DeepInfra API for model inference

### Communication

- REST API
- WebSocket
