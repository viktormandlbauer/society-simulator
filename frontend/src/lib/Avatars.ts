import blue from "@/assets/avatars/blue.png";
import red from "@/assets/avatars/red.png";
import yellow from "@/assets/avatars/yellow.png";
import purple from "@/assets/avatars/purple.png";
import brown from "@/assets/avatars/brown.png";
import orange from "@/assets/avatars/orange.png";
import pink from "@/assets/avatars/pink.png";
import turquoise from "@/assets/avatars/turquoise.png";
import white from "@/assets/avatars/white.png";
import black from "@/assets/avatars/black.png";

export type AvatarId =
    | "blue"
    | "red"
    | "yellow"
    | "purple"
    | "brown"
    | "orange"
    | "pink"
    | "turquoise"
    | "white"
    | "black";

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
    {id: "brown", label: "Brown", imageUrl: brown.src},
    {id: "orange", label: "Orange", imageUrl: orange.src},
    {id: "pink", label: "Pink", imageUrl: pink.src},
    {id: "turquoise", label: "Turquoise", imageUrl: turquoise.src},
    {id: "white", label: "White", imageUrl: white.src},
    {id: "black", label: "Black", imageUrl: black.src},
];

export function getAvatarConfigById(id: AvatarId): AvatarConfig {
    return AVATARS.find(avatar => avatar.id === id) ?? AVATARS[0];
}