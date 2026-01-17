import {proxyToSpring} from "@/app/api/_utils/springProxy";

export async function POST(request: Request, ctx: { params: Promise<{ lobbyId: string }> }) {
    const { lobbyId } = await ctx.params;
    return proxyToSpring(request, `/api/lobbies/${lobbyId}/join`);
}