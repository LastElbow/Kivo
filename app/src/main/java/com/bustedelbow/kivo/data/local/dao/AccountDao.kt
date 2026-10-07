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

    /**
     * Archives the Account with [id], hiding it from active reads while keeping its Entries readable
     * (ADR-0004).
     */
    @Query("UPDATE accounts SET archived = 1 WHERE id = :id")
    suspend fun archiveById(id: Long)

    /**
     * Hard-deletes the Account with [id] only when no Entry references it — as the Account it
     * touches or as a Transfer's destination — in one statement, so the guard cannot race an
     * insert. Returns the rows removed: 1, or 0 when the Account is referenced or absent
     * (ADR-0004).
     */
    @Query(
        "DELETE FROM accounts WHERE id = :id " +
            "AND NOT EXISTS (SELECT 1 FROM entries WHERE accountId = :id OR counterAccountId = :id)",
    )
    suspend fun hardDeleteIfUnreferenced(id: Long): Int
}
