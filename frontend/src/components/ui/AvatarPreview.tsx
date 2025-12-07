import {AvatarIcon} from "@/components/ui/AvatarIcon";
import {getAvatarConfigById, type AvatarId} from "@/lib/avatars";

interface AvatarPreviewProps {
    id: AvatarId;
    className?: string;
}

export function AvatarPreview({ id, className = "" }: AvatarPreviewProps) {
    const avatar = getAvatarConfigById(id);

    return (
        <div className={`flex flex-col items-center ${className}`}>
            <div className="nes-container is-rounded is-dark flex items-center justify-center size-40">
                <AvatarIcon avatarId={id} size="large" />
            </div>
            <span className="text-xs mt-2">{avatar.label}</span>
        </div>
    )
}