import type { Metadata } from "next";
import "./globals.css";
import "nes.css/css/nes.min.css";
import { Press_Start_2P } from "next/font/google";

export const metadata: Metadata = {
  title: "Society Simulator"
};

// Load the retro font
const pressStart = Press_Start_2P({
  weight: "400",
  subsets: ["latin"],
  display: "swap",
});

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className="min-h-dvh bg-white text-slate-900 dark:bg-slate-950 dark:text-slate-100">
        {/* Use pressStart.className only where you want the NES look */}
        {children}
      </body>
    </html>
  );
}
