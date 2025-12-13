import type {ButtonHTMLAttributes, ReactNode} from "react";

export type NesButtonVariant =
    | "primary"
    | "success"
    | "warning"
    | "error";

// interface is for hand over button props and adding variant and children props
export interface NesButtonProps
    extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: NesButtonVariant;
    children: ReactNode;
}

// NES.css styled button component with variant support
// variant is for different button styles like primary, success, warning, error, disabled
export function NesButton({
                              variant = "primary",
                              className = "",
                              disabled,
                              children,
                              ...props
                          }: NesButtonProps) {
    const variantClass =
        variant === "primary" ? "is-primary" :
            variant === "success" ? "is-success" :
                variant === "warning" ? "is-warning" :
                    variant === "error" ? "is-error" :
                        "";

    const disabledClass = disabled ? "is-disabled" : "";

    return (
        <button
            {...props}
            className={`nes-btn ${variantClass} ${disabledClass} ${className}`.trim()}
        >
            {children}
        </button>
    );
}