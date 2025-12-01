import type {Metadata} from "next";
import "../styles/globals.css";
import {Press_Start_2P} from "next/font/google";
import {ReactNode} from "react";

const pressStart2P = Press_Start_2P({
    subsets: ["latin"],
    weight: ["400"],
});

export const metadata: Metadata = {
    title: "Society Simulator",
    description: "A society simulation game built with Next.js and Tailwind CSS",
};

export default function RootLayout({children,}: { children: ReactNode }) {

    return (
        <html lang="en">
        <body
            className={`${pressStart2P.className} min-h-screen bg-slate-900 text-slate-100 flex justify-center items-start `}
        >
        <main className="w-full max-w-5xl p-4 md:p-8">
            {children}
        </main>
        </body>
        </html>
    )
}