# Kivo

Kivo is an Android-first personal budget tracker. A single user records the movement
of their own money across the places they keep it, and reads back what they have spent
and what they have left. This glossary fixes the vocabulary the rest of the project uses.

## Money containers

**Account**:
A place where the user's money is held — a bank, an e-wallet, cash, or another container.
_Avoid_: Wallet, place, pocket, container

**Account type**:
The kind of an Account, such as Bank, E-Wallet, Cash, or Other.

**Opening balance**:
The amount an Account is known to hold at the moment it is created.

**Balance**:
The amount of money an Account currently holds. A Balance is always derived from the
Account's Opening balance and its Entries; it is never set directly.
_Avoid_: Amount, total

## Movement

**Entry**:
A single recorded movement of money.
_Avoid_: Transaction, record, item, movement

**Expense**:
An Entry that reduces the money in one Account.

**Income**:
An Entry that increases the money in one Account.

**Transfer**:
An Entry that moves money between two Accounts. A Transfer changes no Account's total
and is never counted as Spend.
_Avoid_: Move, top-up, deposit

**Adjustment**:
An Entry that corrects an Account's Balance to a real-world value the user observes.
An Adjustment is not counted as Spend and belongs to no Category.

## Organisation

**Category**:
A user-defined label describing what an Expense or Income is for. Every Category is
either an Expense Category or an Income Category.
_Avoid_: Tag, label, type

**Archived**:
Describes an Account or Category that is hidden from new use but retained so that past
Entries stay intact. Archiving is the ordinary end of an Account or Category's life.
_Avoid_: Deleted, inactive, disabled

**Hard delete**:
The permanent removal of an Account or Category. Offered only for one that no Entry
references, because deleting a referenced one would rewrite history (ADR-0004).
_Avoid_: Delete, remove

## Reading the money

**Period**:
The time window a summary covers, such as a Week, a Month, or a custom range.

**Spend**:
The sum of Expense Entries within a Period. Transfers and Adjustments are never Spend.
_Avoid_: Total spend, outflow
