# Local-first, with no backend or login

Kivo stores everything on the device (SQLite via Room) and has no server, account, or
login. Nothing in the product needs a server, and local-first keeps the app private,
offline, and cheap to run; the cost is that the device is the only copy of the data.
Adding sync later means building a sync/conflict layer, so the data model is kept free
of server assumptions until then.

## Consequences

- Losing or wiping the device loses all data, which is why export/import is in scope.
- No recovery, no multi-device, no server-side features without revisiting this.
