
INSERT INTO clients (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    post_logout_redirect_uris,
    scopes,
    client_settings,
    token_settings
)
VALUES (
    '16b14c6b-4fb0-4c55-91bd-f47c2098b9f4',
    'client-registrar',
    NOW(),
    '{noop}1b7e0043-d2f4-4536-8c9a-79d8b5c43e21',
    'Client Registrar',
    'client_secret_basic',
    'client_credentials',
    NULL,
    NULL,
    'client.create,client.read',
    -- Default ClientSettings (JSON representation)
    '{"requireProofKey":false,"requireAuthorizationConsent":false}',
    -- Default TokenSettings (JSON representation)
    '{"accessTokenFormat":{"value":"opaque"},"reuseRefreshTokens":true,"idTokenSignatureAlgorithm":{"value":"RS256"},"accessTokenTimeToLive":300,"refreshTokenTimeToLive":3600,"deviceCodeTimeToLive":300}'
);

