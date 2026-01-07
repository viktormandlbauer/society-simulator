"use client"

import {AvatarId, getAvatarConfigById} from "@/shared/avatars";
import {useMemo} from "react";
import {NesButton} from "@/shared/ui/NesButton";

type SessionViewModel = {
    name: string;
    avatarId: AvatarId;
};

type Props = {
    session: SessionViewModel;
    isLoading: boolean;
    onRefresh: () => void;
    onLeaveSession: () => void;
};

export function LobbyListHeader({session, isLoading, onRefresh, onLeaveSession}: Props) {
    const avatarConfig = useMemo(() => getAvatarConfigById(session.avatarId), [session.avatarId]);

    return (
        <div className="flex items-center justify-between gap-4">
            <span className="flex items-center gap-2">
                Logged in as {session.name}
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img
                    src={avatarConfig.imageUrl}
                    alt={avatarConfig.label}
                    className="inline-block w-10 h-10 ml-2 align-middle"
                />
            </span>

            <div className="flex items-center gap-2">
                <NesButton variant="primary" onClick={onRefresh} disabled={isLoading}>
                    Refresh
                </NesButton>

                <NesButton variant="warning" onClick={onLeaveSession} disabled={isLoading}>
                    Logout
                </NesButton>
            </div>
        </div>
    );
}