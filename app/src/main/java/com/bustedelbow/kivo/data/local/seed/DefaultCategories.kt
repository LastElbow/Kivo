package com.bustedelbow.kivo.data.local.seed

import com.bustedelbow.kivo.domain.model.CategoryType

/** One default Category written on first run. */
internal data class SeedCategory(
    val name: String,
    val type: CategoryType,
)

/**
 * The small default set of Expense and Income Categories seeded on a fresh install (issue #3), so
 * a first-run user can record an Entry without managing Categories first.
 */
internal object DefaultCategories {
    val all: List<SeedCategory> = listOf(
        SeedCategory(name = "Food", type = CategoryType.EXPENSE),
        SeedCategory(name = "Transport", type = CategoryType.EXPENSE),
        SeedCategory(name = "Bills", type = CategoryType.EXPENSE),
        SeedCategory(name = "Shopping", type = CategoryType.EXPENSE),
        SeedCategory(name = "Other", type = CategoryType.EXPENSE),
        SeedCategory(name = "Salary", type = CategoryType.INCOME),
        SeedCategory(name = "Allowance", type = CategoryType.INCOME),
        SeedCategory(name = "Other", type = CategoryType.INCOME),
    )
}
