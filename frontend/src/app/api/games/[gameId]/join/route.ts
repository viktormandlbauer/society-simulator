import { proxyToSpring } from "@/app/api/_utils/springProxy";

export async function POST(
    request: Request,
    { params }: { params: Promise<{ gameId: string }> }
) {
    const { gameId } = await params;
    return proxyToSpring(request, `/api/games/${gameId}/join`);
}
