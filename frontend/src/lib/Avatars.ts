import blue from "@/assets/avatars/blue.png";
import red from "@/assets/avatars/red.png";
import yellow from "@/assets/avatars/yellow.png";
import purple from "@/assets/avatars/purple.png";

export type AvatarId =
    | "blue"
    | "red"
    | "yellow"
    | "purple";

export interface AvatarConfig {
    id: AvatarId;
    label: string;
    imageUrl: string;
}

export const AVATARS: AvatarConfig[] = [
    {id: "blue", label: "Blue", imageUrl: blue.src},
    {id: "red", label: "Red", imageUrl: red.src},
    {id: "yellow", label: "Yellow", imageUrl: yellow.src},
    {id: "purple", label: "Purple", imageUrl: purple.src},
];

export function getAvatarConfigById(id: AvatarId): AvatarConfig {
    return AVATARS.find(avatar => avatar.id === id) ?? AVATARS[0];
}