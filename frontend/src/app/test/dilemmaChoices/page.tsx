"use client";

import Link from "next/link";
import {DilemmaChoices, DUMMY_DILEMMA} from "@/components/ui/DilemmaChoices";

export default function DilemmaChoicesTestPage() {
    return (
            <DilemmaChoices data={DUMMY_DILEMMA} />
    );
}
