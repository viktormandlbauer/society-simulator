import fs from "fs";
import path from "path";
import Link from "next/link";

type TestPage = {
    href: string;
    label: string;
};

async function getTestPages(): Promise<TestPage[]> {
    const baseDir = path.join(process.cwd(), "src", "app", "test");
    const entries = await fs.promises.readdir(baseDir, {withFileTypes: true});

    const pages: TestPage[] = [];
    for (const entry of entries) {
        if (!entry.isDirectory()) continue;
        if (entry.name.startsWith("_")) continue;

        const pageFile = path.join(baseDir, entry.name, "page.tsx");
        try {
            await fs.promises.access(pageFile, fs.constants.F_OK);
            pages.push({
                href: `/test/${entry.name}`,
                label: entry.name.replace(/-/g, " "),
            });
        } catch {
            // ignore folders without a page.tsx
        }
    }

    return pages.sort((a, b) => a.label.localeCompare(b.label));
}

export default async function TestIndexPage() {
    const testPages = await getTestPages();

    return (
        <div className="nes-container with-title is-rounded is-dark">
            <p className="title">Component Test Pages</p>
            <p className="text-xs text-slate-300 mb-4">
                Quick links to inspect UI components in isolation.
            </p>

            {testPages.length === 0 ? (
                <p className="text-xs text-slate-400">No test pages found.</p>
            ) : (
                <ul className="list-disc list-inside space-y-2">
                    {testPages.map((page) => (
                        <li key={page.href}>
                            <Link href={page.href} className="nes-btn">
                                {page.label}
                            </Link>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
