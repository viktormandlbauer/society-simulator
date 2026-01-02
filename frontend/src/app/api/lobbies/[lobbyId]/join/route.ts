import {proxyToSpring} from "@/app/api/_utils/springProxy";

type Params = { lobbyId: string };

export async function POST(request: Request, ctx: { params: Params }) {
    const { lobbyId } = ctx.params;
    return proxyToSpring(request, `/api/lobbies/${lobbyId}/join`);
}