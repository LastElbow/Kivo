# Entries are the source of truth; balances are derived

The user records individual Entries, and every Account Balance is computed from that
Account's Opening balance plus its Entries. We did not store a mutable balance on the
Account, because the product's core questions — "how much did I spend this week" and
"where does my money stand" — need per-Entry detail that a balance snapshot cannot
reconstruct. Transfers move money between two Accounts and Adjustments correct a
Balance; neither counts as Spend.

## Consequences

- Balance is a query, never a stored field, so it can never drift from its Entries.
- Deleting an Account must not cascade to its Entries, or a Transfer's counterpart
  would vanish and corrupt the other Account's Balance.
- "Spend" excludes Transfers and Adjustments by definition.
