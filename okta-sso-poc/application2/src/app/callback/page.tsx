"use client";
import React from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { redirect_uri, client_id } from "@/utils/const";

export default function Callback() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const code = searchParams.get("code");
  const [token, setToken] = React.useState<string | null>(null);

  React.useEffect(() => {
    const myHeaders = new Headers();
    myHeaders.append("Accept", "application/json");
    myHeaders.append("Content-Type", "application/x-www-form-urlencoded");

    const urlencoded = new URLSearchParams();
    urlencoded.append("grant_type", "authorization_code");
    urlencoded.append("redirect_uri", redirect_uri);
    urlencoded.append("code", code!);
    urlencoded.append(
      "code_verifier",
      "b917f12e6dde0e83f6ab65e4e66fce9f0f30247f330e58ed2934123ad2249abe"
    );
    urlencoded.append("client_id", client_id);

    const requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: urlencoded,
    };

    fetch(
      "https://circlekeu-dev.oktapreview.com/oauth2/aus4iqkj90GFvJmgd0x7/v1/token",
      requestOptions
    )
      .then((response) => response.json())
      .then((result) => {
        setToken(result.access_token);
      })
      .catch((error) => console.error(error));
  }, [code]);

  return (
    <div>
      <h1>Callback Page!</h1>
      <p>{token}</p>
      <br />
      <button
        onClick={() => router.push("/")}
        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-700"
      >
        Go to Home
      </button>
    </div>
  );
}
