import { OKTA_URL } from "./const";

interface AuthorizeCallData {
  sid?: string;
  sessionToken?: string;
  client_id: string;
  redirect_uri: string;
  code_challenge: string;
}

export const makeAuthorizeCall = async ({
  sid,
  sessionToken,
  client_id,
  redirect_uri,
  code_challenge,
}: AuthorizeCallData) => {
  const headers = new Headers();
  const queryParams = new URLSearchParams({
    client_id,
    response_type: "code",
    scope: "openid",
    redirect_uri,
    state: "123",
    code_challenge,
    code_challenge_method: "S256",
  });

  if (sessionToken) {
    queryParams.append("sessionToken", sessionToken);
  }

  if (sid) {
    headers.append("Cookie", `sid=${sid}`);
  }

  const authorizeResponse = await fetch(
    `${OKTA_URL}/oauth2/aus4iqkj90GFvJmgd0x7/v1/authorize?${queryParams.toString()}`,
    { method: "GET", redirect: "manual", headers }
  );

  const locationHeader = authorizeResponse.headers.get("location")?.valueOf()!;
  const cookieHeader = authorizeResponse.headers.get("set-cookie")?.valueOf()!;

  const sidCookieRegex = /sid=([^;]+)/;
  const sidMatches = cookieHeader.match(sidCookieRegex)!;
  const sidCookie = sidMatches[1];

  const codeRegex = /[?&]code=([^&]+)/;
  const matches = locationHeader.match(codeRegex)!;
  const code = matches[1];

  return { sidCookie, code };
};
