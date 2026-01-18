"use client";

import { useEffect, useRef, useState, useCallback } from "react";
import { io, Socket } from "socket.io-client";
import type { ChatMessageDto, ChatMessageRequestDto } from "@/features/lobby/api/chatTypes";

type UseLobbyChat = {
    messages: ChatMessageDto[];
    isConnected: boolean;
    error: string | null;
    sendMessage: (message: string) => void;
    clearMessages: () => void;
};

const SOCKET_URL = "http://localhost:9092";

export function useLobbyChat(token: string | null, lobbyId: string | null): UseLobbyChat {
    const [messages, setMessages] = useState<ChatMessageDto[]>([]);
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const socketRef = useRef<Socket | null>(null);
    const currentLobbyIdRef = useRef<string | null>(null);

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

    return {
        messages,
        isConnected,
        error,
        sendMessage,
        clearMessages,
    };
}
