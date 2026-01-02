import {NesButton} from "@/shared/ui/NesButton";
import {getAvatarConfigById} from "@/shared/avatars";
import {useSessionStore} from "@/features/session/sessionStore";
import {useEffect} from "react";
import {getLobbies, joinLobby, leaveLobby} from "@/features/lobby/api/lobbies";
import {ProblemDetailsError} from "@/shared/http/problemDetails";

export function LobbyListPage() {
    const session = useSessionStore((s) => s.session);
    const clearSession = useSessionStore((s) => s.clearSession);

    // get the avatar config for the current session
    const avatarConfig = session ? getAvatarConfigById(session.avatarId) : null;
    const avatar = avatarConfig ? (
        <img
            src={avatarConfig.imageUrl}
            alt={avatarConfig.label}
            className="inline-block w-10 h-10 ml-2 align-middle"
        />
    ) : null;

    function handleLeaveSession() {
        clearSession();
    }

    // debug: fetch and log the list of lobbies on component mount
    useEffect(() => {
        if (!session?.token) return;

        const lobbyId = "c3ff88ae-d89f-41e9-8e62-ab353664b982";

        leaveLobby(session.token, lobbyId)
            .then(() => console.log("left lobby"))
            .catch((e) => console.error(e instanceof ProblemDetailsError ? e.problem : e));
    }, [session?.token]);

    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Lobby List</p>

            <div className="flex flex-col gap-4">
                <span>
                    Logged in as: {session?.name} {avatar}
                </span>

                <NesButton variant="warning" onClick={handleLeaveSession}>
                    Leave session
                </NesButton>
            </div>
        </div>
    );
}
