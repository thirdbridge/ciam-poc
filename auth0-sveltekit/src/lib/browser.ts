/**
 * This is probably an overkill in this case, but for secure strings,
 * it is better not to use pseudorandom generators like `Math.random()`.
 */
export function generateSecureString(length: number) {
	const array = new Uint8Array(length);
	window.crypto.getRandomValues(array);
	let hexString = '';
	array.forEach((byte) => {
		hexString += byte.toString(16).padStart(2, '0');
	});
	return hexString;
}
