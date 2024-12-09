import { redirect_uri, client_id } from "@/utils/const";

export default function Home() {
  const queryParams = new URLSearchParams({
    client_id,
    redirect_uri,
    state: "123",
    code_challenge: "0cgyW2oInJqGv_Zw4stmyfb3QXq37ObpBGWHGqxIeqM",
    code_challenge_method: "S256",
  });
  return (
    <div>
      <h1>Login - GMAP</h1>
      <a href={`http://localhost:3000?${queryParams.toString()}`}>
        Click here!
      </a>
    </div>
  );
}
