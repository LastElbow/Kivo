package com.bustedelbow.kivo.data.repository

import com.bustedelbow.kivo.data.FakeAccountDao
import com.bustedelbow.kivo.data.FakeEntryDao
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.NewAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [AccountRepository] maps rows to domain Accounts and derives each Balance from its Entries
 * (ADR-0002), and turns a [NewAccount] into a stored Account.
 */
class AccountRepositoryTest {
    private val bank =
        AccountEntity(
            id = 1,
            name = "Bank",
            type = AccountType.BANK.name,
            openingBalanceMinorUnits = 100_000,
        )
    private val cash =
        AccountEntity(
            id = 2,
            name = "Cash",
            type = AccountType.CASH.name,
            openingBalanceMinorUnits = 0,
        )

    @Test
    fun `derives each balance from the opening balance and its entries`() =
        runTest {
            val entries =
                listOf(
                    EntryEntity(
                        id = 1,
                        type = "EXPENSE",
                        accountId = 1,
                        categoryId = 9,
                        amountMinorUnits = 20_000,
                        occurredOnEpochDay = 100,
                    ),
                    EntryEntity(
                        id = 2,
                        type = "TRANSFER",
                        accountId = 1,
                        counterAccountId = 2,
                        amountMinorUnits = 25_000,
                        occurredOnEpochDay = 101,
                    ),
                )
            val repository = AccountRepository(FakeAccountDao(listOf(bank, cash)), FakeEntryDao(entries))

            val balances = repository.observeAccountBalances().first()

            assertEquals(55_000L, balances.first { it.account.id == 1L }.balanceMinorUnits)
            assertEquals(25_000L, balances.first { it.account.id == 2L }.balanceMinorUnits)
        }

    @Test
    fun `createAccount stores the domain account and returns its id`() =
        runTest {
            val accountDao = FakeAccountDao()
            val repository = AccountRepository(accountDao, FakeEntryDao())

            val id =
                repository.createAccount(
                    NewAccount(name = "Cash", type = AccountType.CASH, openingBalanceMinorUnits = 5_000),
                )

            assertEquals(1L, id)
            val stored = accountDao.inserted.single()
            assertEquals("Cash", stored.name)
            assertEquals("CASH", stored.type)
            assertEquals(5_000L, stored.openingBalanceMinorUnits)
        }

    @Test
    fun `archiveAccount hides the account from active balances but retains the row`() =
        runTest {
            val accountDao = FakeAccountDao(listOf(bank, cash))
            val repository = AccountRepository(accountDao, FakeEntryDao())

            repository.archiveAccount(2)

            assertEquals(listOf(1L), repository.observeAccountBalances().first().map { it.account.id })
            val stored = accountDao.observeAll().first()
            assertTrue(stored.first { it.id == 2L }.archived)
        }

    @Test
    fun `hardDeleteAccount refuses an account an entry references`() =
        runTest {
            val accountDao = FakeAccountDao(listOf(bank, cash), referencedAccountIds = setOf(1L))
            val repository = AccountRepository(accountDao, FakeEntryDao())

            assertFalse(repository.hardDeleteAccount(1))

            assertEquals(listOf(1L, 2L), accountDao.observeAll().first().map { it.id })
        }

    @Test
    fun `hardDeleteAccount removes an account no entry references`() =
        runTest {
            val accountDao = FakeAccountDao(listOf(bank, cash))
            val repository = AccountRepository(accountDao, FakeEntryDao())

            assertTrue(repository.hardDeleteAccount(2))

            assertEquals(listOf(1L), accountDao.observeAll().first().map { it.id })
        }
}
