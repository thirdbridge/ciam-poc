<script lang="ts">
	import { generateSecureString } from '$lib/browser';
	import { browser } from '$app/environment';
	import { PUBLIC_CLIENT_ID, PUBLIC_AUDIENCE, PUBLIC_REDIRECT_URI, PUBLIC_ISSUER } from '$env/static/public';

	const urlSearchParams = new URLSearchParams();
	/**
	 * The state parameter is often neglected when implementing the Authorization Code Flow.
	 * However, it is an important Defense in Depth mechanism that helps secure the application against CSRF attacks.
	 * It can also be used to pass some information to the callback page if necessary.
	 * 
	 * https://auth0.com/docs/secure/attack-protection/state-parameters
	 */
	const state = browser ? generateSecureString(20) : '';


	urlSearchParams.set('client_id', PUBLIC_CLIENT_ID);
	urlSearchParams.set('audience', PUBLIC_AUDIENCE);
	urlSearchParams.set('redirect_uri', PUBLIC_REDIRECT_URI);
	urlSearchParams.set('response_type', 'code');
	urlSearchParams.set('state', state);
	urlSearchParams.set('scope', 'profile openid email');

	/**
	 * Even though SameSite=Lax is the default behavior, it is still important to consider which SameSite parameter you want,
	 * especially since SameSite=None is being deprecated by Google.
	 * 
	 * https://blog.google/products/chrome/privacy-sandbox-tracking-protection/
	*/
	function login() {
		document.cookie = `state=${state}; Path=/; SameSite=Lax; Secure`;
		window.location.href = `${PUBLIC_ISSUER}/v1/authorize?${urlSearchParams.toString()}`;
	}
</script>

<h1>Login Page</h1>
<br />
<button on:click={login}>Login</button>
