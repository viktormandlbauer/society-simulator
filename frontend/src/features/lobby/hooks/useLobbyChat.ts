"use client";

import { useEffect, useRef, useState, useCallback } from "react";
import { io, Socket } from "socket.io-client";
import type { ChatMessageDto, ChatMessageRequestDto } from "@/features/lobby/api/chatTypes";

type VoteResult = {
    roundNumber: number;
    accepted: boolean;
    roundCompleted: boolean;
    counts: Record<number, number>;
    nextDilemma: unknown | null;
    outcomeSummary: string | null;
};

type UseLobbyChat = {
    messages: ChatMessageDto[];
    isConnected: boolean;
    error: string | null;
    sendMessage: (message: string) => void;
    clearMessages: () => void;
    onGameStarted: (callback: (gameId: string) => void) => void;
    joinGameRoom: (gameId: string) => void;
    onVoteCompleted: (callback: (voteResult: VoteResult) => void) => void;
};

const SOCKET_URL = "http://localhost:9092";

export function useLobbyChat(token: string | null, lobbyId: string | null): UseLobbyChat {
    const [messages, setMessages] = useState<ChatMessageDto[]>([]);
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const socketRef = useRef<Socket | null>(null);
    const currentLobbyIdRef = useRef<string | null>(null);
    const gameStartedCallbackRef = useRef<((gameId: string) => void) | null>(null);
    const voteCompletedCallbackRef = useRef<((voteResult: VoteResult) => void) | null>(null);

    // Connect to Socket.IO server
    useEffect(() => {
        if (!token) {
            // No token, disconnect if connected
            if (socketRef.current) {
                socketRef.current.disconnect();
                socketRef.current = null;
            }
            setIsConnected(false);
            return;
        }

        // Create socket connection
        const socket = io(SOCKET_URL, {
            query: { token },
            autoConnect: true,
        });

        socketRef.current = socket;

        // Connection event handlers
        socket.on("connect", () => {
            console.log("Connected to chat server");
            setIsConnected(true);
            setError(null);

            // If we have a lobby ID, join it
            if (currentLobbyIdRef.current) {
                socket.emit("joinLobby", currentLobbyIdRef.current, (response: string) => {
                    console.log("Join lobby response:", response);
                });
            }
        });

        socket.on("disconnect", () => {
            console.log("Disconnected from chat server");
            setIsConnected(false);
        });

        socket.on("connect_error", (err) => {
            console.error("Connection error:", err);
            setError("Failed to connect to chat server");
            setIsConnected(false);
        });

        // Chat event handlers
        socket.on("receiveMessage", (message: ChatMessageDto) => {
            setMessages((prev) => [...prev, message]);
        });

        socket.on("error", (errorMessage: string) => {
            console.error("Chat error:", errorMessage);
            setError(errorMessage);
        });

        // Listen for game started event
        socket.on("gameStarted", (gameId: string) => {
            console.log("Game started event received:", gameId);
            if (gameStartedCallbackRef.current) {
                gameStartedCallbackRef.current(gameId);
            }
        });

        // Listen for vote completed event
        socket.on("voteCompleted", (voteResult: VoteResult) => {
            console.log("🔔 WebSocket: Vote completed event received at socket level:", voteResult);
            if (voteCompletedCallbackRef.current) {
                console.log("✅ Callback exists, invoking it");
                voteCompletedCallbackRef.current(voteResult);
            } else {
                console.warn("⚠️ No callback registered for voteCompleted event!");
            }
        });

        // Cleanup on unmount
        return () => {
            socket.disconnect();
            socketRef.current = null;
        };
    }, [token]);

    // Handle lobby changes
    useEffect(() => {
        const socket = socketRef.current;
        if (!socket || !socket.connected) {
            currentLobbyIdRef.current = lobbyId;
            return;
        }

        // Leave previous lobby if we were in one
        if (currentLobbyIdRef.current && currentLobbyIdRef.current !== lobbyId) {
            socket.emit("leaveLobby", "", (response: string) => {
                console.log("Leave lobby response:", response);
            });
            setMessages([]); // Clear messages when leaving lobby
        }

        // Join new lobby if we have one
        if (lobbyId) {
            socket.emit("joinLobby", lobbyId, (response: string) => {
                console.log("Join lobby response:", response);
                setError(null);
            });
        }

        currentLobbyIdRef.current = lobbyId;
    }, [lobbyId]);

    const sendMessage = useCallback((message: string) => {
        const socket = socketRef.current;
        if (!socket || !socket.connected) {
            setError("Not connected to chat server");
            return;
        }

        if (!currentLobbyIdRef.current) {
            setError("Not in a lobby");
            return;
        }

        if (!message.trim()) {
            return;
        }

        if (message.length > 500) {
            setError("Message too long (max 500 characters)");
            return;
        }

        const request: ChatMessageRequestDto = { message: message.trim() };
        socket.emit("sendMessage", request);
        setError(null);
    }, []);

    const clearMessages = useCallback(() => {
        setMessages([]);
    }, []);

    const onGameStarted = useCallback((callback: (gameId: string) => void) => {
        gameStartedCallbackRef.current = callback;
    }, []);

    const joinGameRoom = useCallback((gameId: string) => {
        const socket = socketRef.current;
        if (!socket || !socket.connected) {
            console.warn("Cannot join game room: socket not connected");
            return;
        }

        console.log("🎮 Joining game room:", gameId);
        socket.emit("joinGame", gameId, (response: string) => {
            console.log("✅ Join game room response:", response);
        });
    }, []);

    const onVoteCompleted = useCallback((callback: (voteResult: VoteResult) => void) => {
        console.log("📝 Registering vote completed callback");
        voteCompletedCallbackRef.current = callback;
    }, []);

    return {
        messages,
        isConnected,
        error,
        sendMessage,
        clearMessages,
        onGameStarted,
        joinGameRoom,
        onVoteCompleted,
    };
}
