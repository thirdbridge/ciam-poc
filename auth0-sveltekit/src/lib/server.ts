// place files you want to import through the `$lib` alias in this folder.
import jwt, { type JwtPayload, type VerifyOptions } from 'jsonwebtoken';
import jwksClient from 'jwks-rsa';
import assert from 'assert';
import { ISSUER, CLIENT_ID, CLIENT_SECRET, AUDIENCE } from '$env/static/private';

const client = jwksClient({
	jwksUri: `${ISSUER}/.well-known/jwks.json`
});

const getKey: jwt.GetPublicKeyOrSecret = (header, callback) => {
	client.getSigningKey(header.kid, function (err, key) {
		var signingKey = key?.getPublicKey();
		callback(null, signingKey);
	});
};

export const verify = (accessToken: string): Promise<JwtPayload> => {
	const options: VerifyOptions = {
		audience: AUDIENCE,
		issuer: `${ISSUER}/`,
		algorithms: ['RS256']
	};
	/**
	 * NEVER PERFORM YOUR OWN CRYPTOGRAPHIC MANIPULATIONS. Always delegate those tasks to battle-proven libraries.
	 */
	return new Promise((resolve, reject) => {
		jwt.verify(accessToken, getKey, options, (err, decoded) => {
			if (err) {
				reject(err);
			}

			resolve(decoded as JwtPayload);
		});
	});
};

export const validatePermissions = (permissions: { expected: string[]; granted: string[] }) => {
	const { expected: expectedPermissions, granted: grantedPermissions = [] } = permissions;
	for (const expected of expectedPermissions) {
		const granted = grantedPermissions.find((granted) => granted === expected);
		assert(granted);
	}
};

/**
 * The same Auth0 application can be used to implement both the Authorization Code Flow and the Client Credentials Flow.
 * In this case, we are reusing the application to fetch the list of users.
 */
export const getAuth0ApiToken = async () => {
	const response = await fetch(`${ISSUER}/oauth/token`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({
			grant_type: 'client_credentials',
			client_id: CLIENT_ID,
			client_secret: CLIENT_SECRET,
			audience: `${ISSUER}/api/v2/`
		})
	});

	const data = await response.json();
	return data.access_token;
};
