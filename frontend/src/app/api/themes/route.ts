import { proxyToSpring } from "@/app/api/_utils/springProxy";

export async function GET(request: Request) {
    return proxyToSpring(request, "/api/themes");
}
