# Archive Accounts and Categories instead of deleting them

An Account or Category that is referenced by history is archived, not deleted: it
disappears from pickers and default totals but keeps its Entries readable. Hard delete
is offered only for items nothing references. Cascading deletes would silently rewrite
past totals and, for a Transfer, destroy the counterpart Entry and corrupt the other
Account's Balance — the exact failure that makes a budgeting app untrustworthy.

## Consequences

- Queries filter out Archived items by default but must still be able to resolve them
  for historical Entries.
- Renaming is safe and rewrites history everywhere, because Entries reference an
  Account or Category by stable id.
