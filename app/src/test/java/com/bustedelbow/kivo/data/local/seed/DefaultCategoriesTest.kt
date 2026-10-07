package com.bustedelbow.kivo.data.local.seed

import com.bustedelbow.kivo.domain.model.CategoryType
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guards the categories a fresh install must have (issue #3). */
class DefaultCategoriesTest {

    @Test
    fun `seeds both expense and income categories`() {
        val types = DefaultCategories.all.map { it.type }.toSet()

        assertTrue("expected an Expense Category", types.contains(CategoryType.EXPENSE))
        assertTrue("expected an Income Category", types.contains(CategoryType.INCOME))
    }
}
