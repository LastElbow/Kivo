package com.bustedelbow.kivo.domain.model

/**
 * A user-defined label describing what an Expense or Income is for (GLOSSARY: Category).
 *
 * Every Category is either an Expense Category or an Income Category. `archived` records that
 * the Category is retired rather than deleted (ADR-0004).
 */
data class Category(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val archived: Boolean = false,
)

/** Whether a Category labels an Expense or an Income. */
enum class CategoryType {
    EXPENSE,
    INCOME,
}
