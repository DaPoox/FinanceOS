package com.daprox.financeos.domain.usecase

import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Transaction
import com.daprox.financeos.domain.repository.TransactionRepository
import java.util.UUID

class AddTransactionUseCase(private val transactionRepo: TransactionRepository) {
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
