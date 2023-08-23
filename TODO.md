# TODOs

- bcrypt instead of hmac
- simulate scenario when a user wants to generate an access token based on credentials
- store tokens in db
  - Similarly, in case of refresh token (JWT or not) — we need to save it in DB to revoke and prevent malicious user access.
  - https://betterprogramming.pub/should-we-store-tokens-in-db-af30212b7f22
  - https://developer.squareup.com/forums/t/multiple-oauth-access-and-refresh-tokens-for-single-app-on-multiple-machines/5777/2
- implement the revoke endpoint
