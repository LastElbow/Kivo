package com.bustedelbow.kivo.data.repository

import com.bustedelbow.kivo.data.local.dao.CategoryDao
import com.bustedelbow.kivo.data.mapper.toDomain
import com.bustedelbow.kivo.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Reads Categories. The default set is seeded by [com.bustedelbow.kivo.data.local.KivoDatabase] on
 * first creation; management arrives in a later slice.
 */
class CategoryRepository(private val categoryDao: CategoryDao) {

    /** The active (non-Archived) Categories, ordered by name. */
    fun observeActiveCategories(): Flow<List<Category>> =
        categoryDao.observeActive().map { entities -> entities.map { it.toDomain() } }
}
