import { NextResponse } from "next/server";

export async function POST(request: Request) {
    const baseUrl = process.env.SPRING_API_URL;
    if (!baseUrl) {
        return NextResponse.json(
            {
                title: "Server misconfigured",
                detail: "SPRING_API_URL is not set",
                status: 500,
            },
            { status: 500 }
        );
    }

    const body = await request.text();
    const upstream = await fetch(`${baseUrl}/api/session/guest`, {
        method: "POST",
        headers: {
            "Content-Type": request.headers.get("content-type") ?? "application/json",
            Accept: "application/json",
        },
        body,
        cache: "no-store",
    });

    const contentType = upstream.headers.get("content-type") ?? "";
    const isJson = contentType.includes("application/json");

    if (isJson) {
        const data = await upstream.json();
        return NextResponse.json(data, { status: upstream.status });
    }

    const text = await upstream.text();
    return new NextResponse(text, { status: upstream.status });
}
