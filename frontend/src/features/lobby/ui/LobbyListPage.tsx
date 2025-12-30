import { NesButton } from "@/shared/ui/NesButton";
import {getAvatarConfigById} from "@/shared/avatars";
import {useSessionStore} from "@/features/session/sessionStore";

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
