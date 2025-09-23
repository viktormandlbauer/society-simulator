"use client";

export default function Home() {
  return (
    <main className="container mx-auto p-8">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <section className="nes-container with-title is-rounded col-span-2">
          <p className="title">Lobby</p>
          <div className="flex gap-2">
            <button className="nes-btn is-primary">Start</button>
            <button className="nes-btn is-warning">Settings</button>
          </div>
          <input type="text" className="nes-input mt-4" placeholder="Enter name" />
        </section>
      </div>
    </main>
  );
}
