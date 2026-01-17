import { proxyToSpring } from "@/app/api/_utils/springProxy";

export async function POST(
    request: Request,
    { params }: { params: Promise<{ lobbyId: string }> }
) {
    const { lobbyId } = await params;
    return proxyToSpring(request, `/api/lobbies/${lobbyId}/start`);
}
