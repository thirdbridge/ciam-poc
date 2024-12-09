import { OKTA_URL } from "./const";

interface AuthnCallData {
  email: string;
  password: string;
}

export const makeAuthnCall = async ({ email, password }: AuthnCallData) => {
  const authnHeaders = new Headers();
  authnHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    username: email,
    password: password,
    context: {
      deviceToken: "12345",
    },
  });

  const response = await fetch(`${OKTA_URL}/api/v1/authn`, {
    method: "POST",
    headers: authnHeaders,
    body: raw,
  });
  const data = (await response.json()) as { sessionToken?: string };

  if (!data.sessionToken) {
    throw new Error("Invalid credentials");
  }
  return { sessionToken: data.sessionToken };
};
