import { usePlayerSession } from "@/features/player/PlayerSessionContext";
import { NesButton } from "@/components/ui/NesButton";
import {getAvatarConfigById} from "@/lib/avatars";

export function LobbyListPage() {
    const { session, setSession } = usePlayerSession();
    // get the avatar config for the current session
    const avatarConfig = session ? getAvatarConfigById(session.avatarId) : null;
    const avatar = avatarConfig ? (
        <img
            src={avatarConfig.imageUrl}
            alt={avatarConfig.label}
            className="inline-block w-10 h-10 ml-2 align-middle"
        />
    ) : null;


    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Lobby List</p>

            <div className="flex flex-col gap-4">
                <span>
                    Logged in as: {session?.name} {avatar}
                </span>

                {/* Temporary: lets us test switching back to StartPage without refresh */}
                <NesButton variant="warning" onClick={() => setSession(null)}>
                    Leave session
                </NesButton>
            </div>
        </div>
    );
}
