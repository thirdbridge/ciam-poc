import { redirect, type ServerLoad } from '@sveltejs/kit';
import { verify, validatePermissions, getAuth0ApiToken } from '$lib/server';
import { ISSUER } from '$env/static/private';
import assert from 'assert';

export const load: ServerLoad = async ({ cookies }) => {
	const accessToken = cookies.get('access_token')!;

	try {
		const token = await verify(accessToken);
		await validatePermissions({ expected: ['read:users'], granted: token.permissions });
		const apiToken = await getAuth0ApiToken();
		const response = await fetch(`${ISSUER}/api/v2/users`, {
			headers: {
				Authorization: `Bearer ${apiToken}`
			}
		});
		assert(response.status === 200);
		const users = await response.json();
		return { users };
	} catch (e) {
		console.error(e);
		throw redirect(302, '/home');
	}
};
