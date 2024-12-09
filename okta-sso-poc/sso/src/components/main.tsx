import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { makeAuthorizeCall } from "@/utils/make-authorize-call";
import { handleSubmit } from "@/actions/handle-submit";

interface Props {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}

export async function MainContent({ searchParams: getSearchParams }: Props) {
  const cookie = await cookies();
  const searchParams = await getSearchParams;

  const sid = cookie.get("sid");
  const client_id = searchParams.client_id as string;
  const redirect_uri = searchParams.redirect_uri as string;
  const code_challenge = searchParams.code_challenge as string;

  if (sid && client_id && redirect_uri && code_challenge) {
    const { code } = await makeAuthorizeCall({
      sid: sid.value,
      client_id,
      redirect_uri,
      code_challenge,
    });

    await new Promise((resolve) => setTimeout(resolve, 5_000));

    redirect(`${redirect_uri}?code=${code}`);
  }

  return (
    <div>
      <form
        action={handleSubmit}
        method="post"
        className="max-w-md mx-auto bg-white p-8 rounded-lg shadow-md mt-4"
      >
        <div className="mb-4">
          <label htmlFor="email" className="block text-gray-700 font-bold mb-2">
            Email:
          </label>
          <input
            defaultValue=""
            type="text"
            id="email"
            name="email"
            required
            className="text-black w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div className="mb-4">
          <label
            htmlFor="password"
            className="block text-gray-700 font-bold mb-2"
          >
            Password:
          </label>
          <input
            defaultValue=""
            type="password"
            id="password"
            name="password"
            required
            className="text-black w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <input type="hidden" name="client_id" value={client_id} />
        <input type="hidden" name="redirect_uri" value={redirect_uri} />
        <input type="hidden" name="code_challenge" value={code_challenge} />
        <button
          type="submit"
          className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition duration-300"
        >
          Submit
        </button>
      </form>
    </div>
  );
}
