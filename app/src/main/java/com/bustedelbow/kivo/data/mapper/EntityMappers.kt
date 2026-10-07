package com.bustedelbow.kivo.data.mapper

import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.AdjustmentEntry
import com.bustedelbow.kivo.domain.model.Category
import com.bustedelbow.kivo.domain.model.CategoryType
import com.bustedelbow.kivo.domain.model.Entry
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.ExpenseEntry
import com.bustedelbow.kivo.domain.model.IncomeEntry
import com.bustedelbow.kivo.domain.model.TransferEntry

/**
 * Translates between Room rows and the domain types, keeping domain types out of the entities.
 * Enum columns are stored as their `name`, so these are the only places that know the encoding.
 */
fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = AccountType.valueOf(type),
    openingBalanceMinorUnits = openingBalanceMinorUnits,
    archived = archived,
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = CategoryType.valueOf(type),
    archived = archived,
)

fun EntryEntity.toDomain(): Entry = when (EntryType.valueOf(type)) {
    EntryType.EXPENSE -> ExpenseEntry(
        id = id,
        accountId = accountId,
        categoryId = requireNotNull(categoryId) { "Expense entry $id has no Category" },
        amountMinorUnits = amountMinorUnits,
        occurredOnEpochDay = occurredOnEpochDay,
        note = note,
    )

    EntryType.INCOME -> IncomeEntry(
        id = id,
        accountId = accountId,
        categoryId = requireNotNull(categoryId) { "Income entry $id has no Category" },
        amountMinorUnits = amountMinorUnits,
        occurredOnEpochDay = occurredOnEpochDay,
        note = note,
    )

    EntryType.TRANSFER -> TransferEntry(
        id = id,
        fromAccountId = accountId,
        toAccountId = requireNotNull(counterAccountId) { "Transfer entry $id has no destination" },
        amountMinorUnits = amountMinorUnits,
        occurredOnEpochDay = occurredOnEpochDay,
        note = note,
    )

    EntryType.ADJUSTMENT -> AdjustmentEntry(
        id = id,
        accountId = accountId,
        deltaMinorUnits = amountMinorUnits,
        occurredOnEpochDay = occurredOnEpochDay,
        note = note,
    )
}
