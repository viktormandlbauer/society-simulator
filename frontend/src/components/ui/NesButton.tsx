import type {ButtonHTMLAttributes, ReactNode} from "react";
export type NesButtonVariant =
    | "primary"
    | "success"
    | "warning"
    | "error"
    | "disabled";

// interface is for hand over button props and adding variant and children props
export interface NesButtonProps
    extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: NesButtonVariant;
    children: ReactNode;
    onClick?: () => void;
    // onEnter function prop for handling enter button click
}

// NES.css styled button component with variant support
// variant is for different button styles like primary, success, warning, error, disabled
export function NesButton({
    variant = "primary",
    className = "",
    onClick = () => {},
    children,
    ...props
}: NesButtonProps) {
    const variantClass =
        variant === "primary" ? "is-primary" :
        variant === "success" ? "is-success" :
        variant === "warning" ? "is-warning" :
        variant === "error" ? "is-error" :
        variant === "disabled" ? "is-disabled" : "";

    const handleClick = () => {
        console.log("Enter button clicked");
        onClick();
    }
    return (
        <button
            {...props}
            className={`nes-btn ${variantClass} ${className}`.trim()}
            onClick={handleClick}
            >
            {children}
        </button>
    );
}