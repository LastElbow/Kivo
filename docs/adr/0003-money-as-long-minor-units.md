# Money is stored as integer minor units

Every amount is stored as a `Long` in minor units (centavos). We rejected `Double`
because binary floating point cannot represent decimal money exactly and errors
accumulate across sums, and we rejected `BigDecimal` because it buys no accuracy we
lack while adding converters and heavier arithmetic.

## Considered options

- `Double` — rejected: rounding error in a ledger that is read as truth.
- `BigDecimal` — rejected: exact but unnecessary overhead; no sub-centavo amounts.

## Consequences

- Formatting and parsing to/from PHP happen only at the UI edge.
- No `Double` may ever touch an amount.
