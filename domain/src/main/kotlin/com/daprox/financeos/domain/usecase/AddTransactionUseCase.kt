package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import java.util.UUID

/**
 * Records a spending transaction against an envelope in a specific month.
 *
 * Validates transaction inputs (amount > 0, envelope selected) and persists
 * the transaction with a generated ID and current timestamp.
 */
class AddTransactionUseCase(private val transactionRepo: TransactionRepository) {
    /**
     * Invokes the use case to create and save a transaction.
     *
     * Validates that:
     * - amount > 0 (no zero or negative spending)
     * - envelopeId is not blank (must select a valid envelope)
     *
     * @param envelopeId ID of the envelope to charge the transaction to
     * @param monthId Month in "YYYY-MM" format
     * @param amount Transaction amount (must be positive)
     * @param note Optional user-provided memo
     * @return [Result.Success] if transaction is created; [Result.Error] with validation failure
     */
    suspend operator fun invoke(
        envelopeId: String,
        monthId: String,
        amount: Double,
        note: String,
    ): Result<Unit> {
        if (amount <= 0) return Result.Error(IllegalArgumentException("Amount must be positive"))
        if (envelopeId.isBlank()) return Result.Error(IllegalArgumentException("Envelope must be selected"))
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            envelopeId = envelopeId,
            monthId = monthId,
            amount = amount,
            note = note,
            date = System.currentTimeMillis(),
        )
        transactionRepo.insert(transaction)
        return Result.Success(Unit)
    }
}
