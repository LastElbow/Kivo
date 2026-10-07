package com.bustedelbow.kivo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import kotlinx.coroutines.flow.Flow

/** Reads and writes [EntryEntity] rows, the source of truth for derived Balances (ADR-0002). */
@Dao
interface EntryDao {
    /** Every Entry, newest first. */
    @Query("SELECT * FROM entries ORDER BY occurredOnEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<EntryEntity>>

    /** The [limit] most recent Entries, newest first, for the Home history list. */
    @Query("SELECT * FROM entries ORDER BY occurredOnEpochDay DESC, id DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<EntryEntity>>

    /** Inserts an Entry and returns its generated id. */
    @Insert
    suspend fun insert(entry: EntryEntity): Long
}
