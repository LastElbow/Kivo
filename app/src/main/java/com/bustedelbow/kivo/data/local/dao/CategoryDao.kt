package com.bustedelbow.kivo.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.bustedelbow.kivo.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/** Reads [CategoryEntity] rows. The default set is seeded on first creation. */
@Dao
interface CategoryDao {
    /** The active (non-Archived) Categories, ordered by name. */
    @Query("SELECT * FROM categories WHERE archived = 0 ORDER BY name COLLATE NOCASE ASC")
    fun observeActive(): Flow<List<CategoryEntity>>

    /**
     * Every Category, including Archived ones, ordered by name, to resolve the Category name on
     * historical Entries (ADR-0004).
     */
    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<CategoryEntity>>
}
