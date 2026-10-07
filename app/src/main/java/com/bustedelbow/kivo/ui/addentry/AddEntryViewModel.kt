package com.bustedelbow.kivo.ui.addentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bustedelbow.kivo.data.repository.AccountRepository
import com.bustedelbow.kivo.data.repository.CategoryRepository
import com.bustedelbow.kivo.data.repository.EntryRepository
import com.bustedelbow.kivo.domain.model.Account
import com.bustedelbow.kivo.domain.model.Category
import com.bustedelbow.kivo.domain.model.EntryType
import com.bustedelbow.kivo.domain.model.EntryValidation
import com.bustedelbow.kivo.domain.model.EntryValidationError
import com.bustedelbow.kivo.domain.model.NewEntry
import com.bustedelbow.kivo.domain.model.matches
import com.bustedelbow.kivo.ui.format.parsePhpToMinorUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

/**
 * Drives the Add Entry flow: the amount, Account, Category, date and note the user is typing, the
 * validation that blocks saving (issue #4), and recording the [NewEntry] through the repository.
 */
class AddEntryViewModel(
    private val entryRepository: EntryRepository,
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val form = MutableStateFlow(AddEntryForm(occurredOnEpochDay = todayEpochDay()))
    private val isSaving = MutableStateFlow(false)
    private val isSaved = MutableStateFlow(false)

    /** The active Categories, retained so [save] can re-validate rather than trust derived state. */
    private val categories: StateFlow<List<Category>> =
        categoryRepository
            .observeActiveCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = emptyList(),
            )

    val uiState: StateFlow<AddEntryUiState> =
        combine(
            form,
            accountRepository.observeActiveAccounts(),
            categories,
        ) { form, accounts, categories ->
            AddEntryUiState(
                type = form.type,
                amount = form.amount,
                accounts = accounts,
                categories = categories.filter { it.matches(form.type) },
                selectedAccountId = form.accountId,
                selectedCategoryId = form.categoryId,
                occurredOnEpochDay = form.occurredOnEpochDay,
                note = form.note,
                todayEpochDay = todayEpochDay(),
                errors = validate(form, categories),
                isLoading = false,
            )
        }.combine(isSaving) { state, saving -> state.copy(isSaving = saving) }
            .combine(isSaved) { state, saved -> state.copy(isSaved = saved) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = AddEntryUiState(occurredOnEpochDay = todayEpochDay(), todayEpochDay = todayEpochDay()),
            )

    /** Switches between recording an Expense and an Income; the old Category no longer applies. */
    fun setType(type: EntryType) {
        form.update { it.copy(type = type, categoryId = null) }
    }

    fun setAmount(amount: String) {
        form.update { it.copy(amount = amount) }
    }

    fun selectAccount(accountId: Long) {
        form.update { it.copy(accountId = accountId) }
    }

    fun selectCategory(categoryId: Long) {
        form.update { it.copy(categoryId = categoryId) }
    }

    fun setDate(epochDay: Long) {
        form.update { it.copy(occurredOnEpochDay = epochDay) }
    }

    fun setNote(note: String) {
        form.update { it.copy(note = note) }
    }

    /** Records the Entry when the form is valid; does nothing otherwise. */
    fun save() {
        val current = form.value
        if (validate(current, categories.value).isNotEmpty()) return

        val amountMinorUnits = parsePhpToMinorUnits(current.amount) ?: return
        val accountId = current.accountId ?: return
        val categoryId = current.categoryId ?: return

        isSaving.value = true
        viewModelScope.launch {
            entryRepository.createEntry(
                NewEntry(
                    type = current.type,
                    amountMinorUnits = amountMinorUnits,
                    accountId = accountId,
                    categoryId = categoryId,
                    occurredOnEpochDay = current.occurredOnEpochDay,
                    note = current.note.trim().ifBlank { null },
                ),
            )
            isSaving.value = false
            isSaved.value = true
        }
    }

    private fun validate(
        form: AddEntryForm,
        categories: List<Category>,
    ): Set<EntryValidationError> =
        EntryValidation.errorsFor(
            type = form.type,
            amountMinorUnits = parsePhpToMinorUnits(form.amount),
            accountId = form.accountId,
            category = categories.firstOrNull { it.id == form.categoryId && it.matches(form.type) },
            occurredOnEpochDay = form.occurredOnEpochDay,
            todayEpochDay = todayEpochDay(),
        )

    private fun todayEpochDay(): Long = LocalDate.now(clock).toEpochDay()

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(
            accountRepository: AccountRepository,
            categoryRepository: CategoryRepository,
            entryRepository: EntryRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AddEntryViewModel(entryRepository, accountRepository, categoryRepository) }
            }
    }
}

/** The editable form behind [AddEntryUiState]; kept as one value so its fields move together. */
private data class AddEntryForm(
    val type: EntryType = EntryType.EXPENSE,
    val amount: String = "",
    val accountId: Long? = null,
    val categoryId: Long? = null,
    val occurredOnEpochDay: Long,
    val note: String = "",
)

/** The state the Add Entry flow renders, including the validation that blocks saving. */
data class AddEntryUiState(
    val type: EntryType = EntryType.EXPENSE,
    val amount: String = "",
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedAccountId: Long? = null,
    val selectedCategoryId: Long? = null,
    val occurredOnEpochDay: Long = 0,
    val note: String = "",
    val todayEpochDay: Long = 0,
    val errors: Set<EntryValidationError> = emptySet(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
) {
    /** Whether the form can be saved as it stands. */
    val canSave: Boolean get() = !isLoading && errors.isEmpty() && !isSaving && !isSaved
}
