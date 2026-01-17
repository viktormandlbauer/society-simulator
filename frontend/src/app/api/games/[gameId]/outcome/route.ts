import { NextRequest } from "next/server";
import { proxyToSpring } from "@/app/api/_utils/springProxy";

export async function GET(
    request: NextRequest,
    { params }: { params: { gameId: string } }
) {
    const { gameId } = params;
    return proxyToSpring(request, `/api/games/${gameId}/outcome`);
}
