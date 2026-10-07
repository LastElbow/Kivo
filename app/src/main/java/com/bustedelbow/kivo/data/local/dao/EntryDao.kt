package com.bustedelbow.kivo.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.bustedelbow.kivo.data.local.entity.EntryEntity
import kotlinx.coroutines.flow.Flow

/** Reads [EntryEntity] rows, the source of truth for derived Balances (ADR-0002). */
@Dao
interface EntryDao {
    /** Every Entry, newest first. */
    @Query("SELECT * FROM entries ORDER BY occurredOnEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<EntryEntity>>
}
