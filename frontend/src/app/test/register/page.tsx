"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { NesButton } from "@/components/ui/NesButton";
import { NesInput } from "@/components/ui/NesInput";
import { getGamemasterId, setGamemasterId } from "@/lib/gamemasterSession";
import { API_BASE_URL } from "@/lib/apiConfig";

type Status = "idle" | "submitting" | "success" | "error";

const API_PATH = "/api/auth/register/gamemaster";

export default function GamemasterRegisterPage() {
  const [username, setUsername] = useState("");
  const [status, setStatus] = useState<Status>("idle");
  const [error, setError] = useState<string | null>(null);
  const [savedId, setSavedId] = useState<string | null>(getGamemasterId());

  const isSubmitting = status === "submitting";
  const canSubmit = username.trim().length > 0 && !isSubmitting;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const trimmedUsername = username.trim();
    if (!trimmedUsername) {
      setStatus("error");
      setError("Username is required.");
      return;
    }

    setStatus("submitting");
    setError(null);

    try {
      const response = await fetch(`${API_BASE_URL}${API_PATH}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: trimmedUsername }),
      });

      if (!response.ok) {
        const message = (await response.text()).trim();
        throw new Error(message || "Registration failed.");
      }

      const payload = await response.json().catch(() => null);
      const newId = payload?.data?.id as string | undefined;
      if (newId) {
        setGamemasterId(newId);
        setSavedId(newId);
      }

      setStatus("success");
    } catch (e) {
      setStatus("error");
      setError(e instanceof Error ? e.message : "Something went wrong.");
    }
  }

  return (
    <div className="nes-container with-title is-rounded is-dark max-w-xl mx-auto">
      <p className="title">Gamemaster Registration</p>
      <p className="text-xs text-slate-300 mb-4">
        Create your gamemaster account with a unique username. (DTO: {"{ username }"})
      </p>

      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
        <label className="text-xs" htmlFor="username">
          Username
        </label>

        <NesInput
          id="username"
          name="username"
          placeholder="Enter username"
          value={username}
          onChange={(e) => {
            setUsername(e.target.value);
            if (status !== "submitting") {
              setStatus("idle");
              setError(null);
            }
          }}
          className="is-dark"
          aria-invalid={Boolean(error)}
          aria-describedby={error ? "username-error" : undefined}
          disabled={isSubmitting}
        />

        {error && (
          <p id="username-error" className="text-xs text-red-400" role="alert">
            {error}
          </p>
        )}
        {status === "success" && (
          <p className="text-xs text-green-400" role="status">
            Registration successful.
          </p>
        )}
        {savedId && (
          <p className="text-xs text-slate-200" role="status">
            Gamemaster ID: {savedId}
          </p>
        )}

        <div className="flex gap-3 items-center">
          <NesButton type="submit" variant={canSubmit ? "success" : "disabled"} disabled={!canSubmit}>
            {isSubmitting ? "Submitting..." : "Register"}
          </NesButton>

          <Link href="/gamemaster/login" className="nes-btn">
            Login
          </Link>
          <Link href="/" className="nes-btn">
            Back to home
          </Link>
        </div>
      </form>
    </div>
  );
}
