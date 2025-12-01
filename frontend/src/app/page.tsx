import {NesButton} from "@/components/ui/NesButton";

export default function Home() {
    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Society Simulator</p>
            <p>Welcome to Society Simulator!</p>
            <NesButton variant="success" style={{ marginRight: 10}}>Enter</NesButton>
        </div>
    );
}