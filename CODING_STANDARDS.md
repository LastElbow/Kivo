# Coding standards

Judgement calls the reviewer enforces. Mechanical style — formatting, import order, naming — belongs to ktlint and lint, not here. Decisions live in `docs/adr/`; vocabulary in `GLOSSARY.md`.

## Compute money through the Ledger

Derive Balances, totals and Spend with the domain `Ledger` (`com.bustedelbow.kivo.domain.ledger`), never by summing amounts in a ViewModel or repository. Reach for bespoke arithmetic only when `Ledger` cannot express the query.

## Keep domain types out of storage

Room entities hold primitives; map them to domain types in `com.bustedelbow.kivo.data.mapper`. Read queries exclude Archived rows by default (ADR-0004).

## Bundle fields that travel together

When three or more fields move through one call chain — a creation form down to its repository — bundle them as a domain value type. `NewAccount` is the precedent.

## Speak the glossary

Name every domain concept as `GLOSSARY.md` defines it. A concept the glossary lacks is either one the project doesn't use (reconsider the name) or a real gap: add it to `GLOSSARY.md` in the same change.
