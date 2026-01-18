"use client";

import { useState, useRef, useEffect } from "react";
import type { ChatMessageDto } from "@/features/lobby/api/chatTypes";
import { NesButton } from "@/shared/ui/NesButton";

type Props = {
    messages: ChatMessageDto[];
    isConnected: boolean;
    error: string | null;
    onSendMessage: (message: string) => void;
};

export function LobbyChat({ messages, isConnected, error, onSendMessage }: Props) {
    const [inputValue, setInputValue] = useState("");
    const messagesEndRef = useRef<HTMLDivElement>(null);
    const messagesContainerRef = useRef<HTMLDivElement>(null);

    // Auto-scroll to bottom when new messages arrive
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (inputValue.trim()) {
            onSendMessage(inputValue);
            setInputValue("");
        }
    };

    const formatTime = (timestamp: string) => {
        const date = new Date(timestamp);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    return (
        <div className="nes-container is-rounded is-dark flex flex-col h-full">
            <div className="flex items-center justify-between mb-2">
                <p>Chat</p>
                <span className={`text-xs ${isConnected ? "nes-text is-success" : "nes-text is-error"}`}>
                    {isConnected ? "● Connected" : "● Disconnected"}
                </span>
            </div>

            {error && (
                <div className="nes-text is-error text-xs mb-2">
                    {error}
                </div>
            )}

            {/* Messages container */}
            <div
                ref={messagesContainerRef}
                className="flex-1 overflow-y-auto mb-3 min-h-[200px] max-h-[300px] border border-gray-600 rounded p-2"
                style={{ scrollbarWidth: 'thin' }}
            >
                {messages.length === 0 ? (
                    <div className="text-xs opacity-50 text-center mt-4">
                        No messages yet. Say hello!
                    </div>
                ) : (
                    <div className="flex flex-col gap-2">
                        {messages.map((msg, index) => (
                            <div key={index} className="text-xs">
                                <div className="flex items-baseline gap-2">
                                    <span className="opacity-50 text-[10px]">
                                        {formatTime(msg.timestamp)}
                                    </span>
                                    <span
                                        className={msg.playerId === null ? "nes-text is-warning font-bold" : "font-bold"}
                                    >
                                        {msg.playerName}:
                                    </span>
                                </div>
                                <div className="ml-12 break-words">
                                    {msg.message}
                                </div>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                )}
            </div>

            {/* Input form */}
            <form onSubmit={handleSubmit} className="flex gap-2">
                <input
                    type="text"
                    className="nes-input is-dark flex-1 text-xs"
                    placeholder="Type a message..."
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    maxLength={500}
                    disabled={!isConnected}
                />
                <NesButton
                    type="submit"
                    variant="primary"
                    disabled={!isConnected || !inputValue.trim()}
                    className="text-xs"
                >
                    Send
                </NesButton>
            </form>

            {inputValue.length > 400 && (
                <div className="text-xs opacity-50 mt-1">
                    {inputValue.length}/500 characters
                </div>
            )}
        </div>
    );
}
