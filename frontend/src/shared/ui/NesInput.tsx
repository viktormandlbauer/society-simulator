import type { InputHTMLAttributes } from "react";

export type NesInputProps = InputHTMLAttributes<HTMLInputElement>;


export function NesInput({className = "", ...props}: NesInputProps) {
    return(
        <input
            {...props}
            className={`nes-input ${className}`.trim()}
        />
    );
}