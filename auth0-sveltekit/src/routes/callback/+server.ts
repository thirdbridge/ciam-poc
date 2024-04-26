import { type RequestHandler } from '@sveltejs/kit';
import { ISSUER, CLIENT_ID, REDIRECT_URI, CLIENT_SECRET } from '$env/static/private';
import assert from 'assert';

export const GET: RequestHandler = async ({ url: { searchParams }, cookies }) => {
	const code = searchParams.get('code')!;
	const stateUrl = searchParams.get('state')!;
	const stateCookie = cookies.get('state');

	console.log('code ->', code);
	console.log('stateUrl ->', stateUrl);
	console.log('stateCookie ->', stateCookie);

	try {
		assert(code);
		assert(stateUrl);
		assert(stateUrl === stateCookie);

		/**
		 * Since we are using the Authorization Code Flow,
		 * this call happens on the server and can safely use the CLIENT_SECRET.
		 */
		const response = await fetch(`${ISSUER}/oauth/token`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: new URLSearchParams({
				grant_type: 'authorization_code',
				client_id: CLIENT_ID,
				client_secret: CLIENT_SECRET,
				code,
				redirect_uri: REDIRECT_URI
			})
		});

		const tokens = await response.json();
		const headers = new Headers();

		/**
		 * Since the Access Token should mostly be used for validation on the server, we can reduce the
		 * blast radius of a hypothetical XSS attack not allowing access to the cookie from the DOM with
		 * the `HttpOnly` parameter.
		 */
		headers.append('Set-Cookie', `access_token=${tokens.access_token}; Path=/; HttpOnly; Secure`);

		/**
		 * On the contrary, the ID Token is designed to be read by the frontend.
		 */
		headers.append('Set-Cookie', `id_token=${tokens.id_token}; Path=/; Secure`);
		headers.append('Location', `/home`);

		return new Response(null, {
			status: 302,
			headers
		});
	} catch (e) {
		console.error(e);
		/**
		 * It is important to use a 302 and not a 301. Otherwise, the browser will continue to redirect
		 * to the /login page as long as the response is in the cache. A temporary redirect, such as 302 or 307,
		 * should be used.
		 */
		return new Response(null, {
			status: 302,
			headers: {
				Location: '/login'
			}
		});
	}
};
