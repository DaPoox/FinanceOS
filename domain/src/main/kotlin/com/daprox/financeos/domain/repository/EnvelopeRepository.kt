package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Envelope
import kotlinx.coroutines.flow.Flow

interface EnvelopeRepository {
    fun getActiveEnvelopes(): Flow<List<Envelope>>
    suspend fun getById(id: String): Envelope?
    suspend fun insert(envelope: Envelope)
    suspend fun update(envelope: Envelope)
    suspend fun softDelete(id: String)
}
