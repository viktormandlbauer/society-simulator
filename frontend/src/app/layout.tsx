import "./globals.css";
import "nes.css/css/nes.min.css";
import { Press_Start_2P } from "next/font/google";

const pressStart = Press_Start_2P({ weight: "400", subsets: ["latin"] });

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={pressStart.className}>
      <body className="bg-gray-100 min-h-screen">{children}</body>
    </html>
  );
}
