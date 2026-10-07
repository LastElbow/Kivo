package com.bustedelbow.kivo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bustedelbow.kivo.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

/** Reads and writes [AccountEntity] rows. Archived Accounts are hidden from pickers by default. */
@Dao
interface AccountDao {
    /** The active (non-Archived) Accounts, ordered by name. */
    @Query("SELECT * FROM accounts WHERE archived = 0 ORDER BY name COLLATE NOCASE ASC")
    fun observeActive(): Flow<List<AccountEntity>>

    /**
     * Every Account, including Archived ones, ordered by name. Used to resolve the Account name on
     * historical Entries, which must stay readable after the Account is archived (ADR-0004).
     */
    @Query("SELECT * FROM accounts ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<AccountEntity>>

    /** Inserts an Account and returns its generated id. */
    @Insert
    suspend fun insert(account: AccountEntity): Long
}
