"use client";
import {NesButton} from "@/components/ui/NesButton";
import {NesInput} from "@/components/ui/NesInput";
import {AvatarIcon} from "@/components/ui/AvatarIcon";

export default function Home() {
    const handleEnter = () => {
        // TODO: Implement enter button functionality
    }

    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Society Simulator</p>
            <p>Welcome to Society Simulator!</p>
            <NesButton variant="success" onEnter={handleEnter} style={{ marginRight: 10}}>Enter</NesButton>
            <NesInput placeholder="Username" style={{ marginRight: 10}} />
            <AvatarIcon avatarId="black" size="large" />
        </div>
    );

}