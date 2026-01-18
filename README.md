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

- Java 21 with Maven
- Spring (Spring Boot)
- WebSocket (real-time communication)
- PostgreSQL

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

## How to Setup

You can run the project **locally** or using **Docker**.

### For local setup:
- **Java 21**
- **Node.js & npm**

### For Docker setup:
- **Docker**
- **Docker Compose**

---

## Environment Variables

You need **two environment files**:
- `.env.local` → used by the **frontend**
- `.env` → used by the **backend**

The `.env.local` file should include: 

SPRING_API_URL=http://localhost:8080

The `.env` file should include:

DEEPINFRA_API_URL=[https://api.deepinfra.com/v1/openai](https://api.deepinfra.com/v1/openai "https://api.deepinfra.com/v1/openai")
DEEPINFRA_API_KEY=<API-KEY>
DEEPINFRA_API_MODEL=meta-llama/Llama-4-Scout-17B-16E-Instruct 
DB_URL=jdbc:postgresql://localhost:5432/society_db 
DB_USERNAME=society_user 
DB_PASSWORD=secure_password 
JWT_SECRET=someverysecuryjwtkey12332131312312312321312

Make sure both files are configured correctly before starting the application.

---

## Run Locally (Without Docker)

### 1. Start the Backend
Run the Spring Boot backend application using Java 21.

### 2. Start the Frontend
```bash
npm install
npm run dev
```
## Run via Docker 

### 1. Build and start all services

`docker compose -f docker-compose.yml up --build --force-recreate -d`

### 2. Stop the application

`docker-compose down`
