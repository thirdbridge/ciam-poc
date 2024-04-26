import { redirect, type ServerLoad } from '@sveltejs/kit';
import { verify } from '$lib/server';

export const load: ServerLoad = async ({ cookies }) => {
	const accessToken = cookies.get('access_token')!;

	try {
		await verify(accessToken);
	} catch (e) {
		throw redirect(302, '/login');
	}
};
