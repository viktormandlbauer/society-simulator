# Society Simulator

A multiplayer web game where players choose a topic and face AI-generated dilemmas.

Each round presents multiple options. Players vote in real time, and the majority decision determines the next scenario.

Built with Spring, React / Next.js, WebSocket, styled with NES.css, and powered by an AI LLaMA model via DeepInfra.

## Gameplay Overview

1. Players choose a topic (e.g. ethics, zombie apocalypse, politics)
2. The AI generates a dilemma scenario
3. Multiple options are presented
4. Players vote in real time
5. The majority vote wins
6. The game continues with the next AI-generated scenario

Every game session evolves differently based on collective player choices.

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
