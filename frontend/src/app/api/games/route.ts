import { proxyToSpring } from "@/app/api/_utils/springProxy";

export async function POST(request: Request) {
    return proxyToSpring(request, "/api/games");
}
