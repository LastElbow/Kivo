package com.bustedelbow.kivo.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import com.bustedelbow.kivo.domain.model.AccountType
import com.bustedelbow.kivo.domain.model.CategoryType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises the real SQLite engine on the JVM through Robolectric: the seed callback inserts the
 * default Categories (issue #3), and the DAOs read and order rows as their queries declare.
 */
@RunWith(AndroidJUnit4::class)
class KivoDatabaseTest {
    private lateinit var database: KivoDatabase

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    KivoDatabase::class.java,
                ).addCallback(KivoDatabase.seedCallback)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `seeds default expense and income categories`() =
        runBlocking {
            val types =
                database
                    .categoryDao()
                    .observeActive()
                    .first()
                    .map { it.type }
                    .toSet()

            assertTrue(types.contains(CategoryType.EXPENSE.name))
            assertTrue(types.contains(CategoryType.INCOME.name))
        }

    @Test
    fun `lists active accounts ordered by name`() =
        runBlocking {
            database.accountDao().insert(
                AccountEntity(
                    name = "Wallet",
                    type = AccountType.E_WALLET.name,
                    openingBalanceMinorUnits = 0,
                ),
            )
            database.accountDao().insert(
                AccountEntity(
                    name = "Bank",
                    type = AccountType.BANK.name,
                    openingBalanceMinorUnits = 100,
                ),
            )

            val names =
                database
                    .accountDao()
                    .observeActive()
                    .first()
                    .map { it.name }

            assertEquals(listOf("Bank", "Wallet"), names)
        }
}
