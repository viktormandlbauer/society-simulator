import {getAvatarConfigById, type AvatarId} from "@/lib/avatars";

export type AvatarSize = "small" | "medium" | "large";


interface AvatarIconProps {
    avatarId: AvatarId;
    size?: AvatarSize; // size in pixels
    className?: string;
}

export function AvatarIcon({
    avatarId,
    size = "medium",
    className = "",
}: AvatarIconProps) {
    const avatar = getAvatarConfigById(avatarId);

    const sizeClass = size === "small" ? "scale-75" : size === "large" ? "scale-200" : "scale-100"

    return (
        <img
            src={avatar.imageUrl}
            alt={avatar.label}
            className={`${sizeClass} ${className}`.trim()}

        />

    );
}