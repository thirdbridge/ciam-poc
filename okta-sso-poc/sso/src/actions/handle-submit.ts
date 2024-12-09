import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { makeAuthorizeCall } from "@/utils/make-authorize-call";
import { makeAuthnCall } from "@/utils/make-authn-call";

export async function handleSubmit(formData: FormData) {
  "use server";

  const cookieStore = await cookies();

  const email = formData.get("email") as string;
  const password = formData.get("password") as string;
  const client_id = formData.get("client_id") as string;
  const redirect_uri = formData.get("redirect_uri") as string;
  const code_challenge = formData.get("code_challenge") as string;

  const { sessionToken } = await makeAuthnCall({ email, password });

  const { code, sidCookie } = await makeAuthorizeCall({
    sessionToken,
    client_id,
    redirect_uri,
    code_challenge,
  });

  cookieStore.set({
    name: "sid",
    value: sidCookie!,
    path: "/",
    sameSite: "lax",
  });

  redirect(`${redirect_uri}?code=${code}`);
}
