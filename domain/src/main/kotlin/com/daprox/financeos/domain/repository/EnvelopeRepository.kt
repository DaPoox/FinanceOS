package com.daprox.financeos.domain.repository

import com.daprox.financeos.domain.model.Envelope
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for envelope (budget category) persistence and retrieval.
 *
 * Handles envelope lifecycle management including soft-deletion for archival.
 * Archived envelopes remain in the database for historical reference without
 * appearing in active budget workflows.
 */
interface EnvelopeRepository {
    /**
     * Observes all active (non-archived) envelopes as a continuous stream.
     *
     * @return Flow emitting the latest list of active envelopes
     */
    fun getActiveEnvelopes(): Flow<List<Envelope>>

    /**
     * Retrieves a single envelope by ID (active or archived).
     *
     * @param id Envelope identifier
     * @return Envelope if found, null otherwise
     */
    suspend fun getById(id: String): Envelope?

    /**
     * Inserts a new envelope.
     *
     * @param envelope Envelope to insert
     */
    suspend fun insert(envelope: Envelope)

    /**
     * Updates an existing envelope.
     *
     * @param envelope Envelope with updated values
     */
    suspend fun update(envelope: Envelope)

    /**
     * Soft-deletes (archives) an envelope by setting isActive to false.
     *
     * The envelope remains in storage for historical record-keeping.
     *
     * @param id Envelope identifier to archive
     */
    suspend fun softDelete(id: String)
}
