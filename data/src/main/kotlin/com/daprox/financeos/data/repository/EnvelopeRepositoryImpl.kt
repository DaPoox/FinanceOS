package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.EnvelopeDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [EnvelopeRepository] using Room database.
 *
 * Handles all database operations for envelopes, delegating to [EnvelopeDao].
 * Transparently converts between [EnvelopeEntity] (database model) and [Envelope] (domain model).
 */
class EnvelopeRepositoryImpl(private val dao: EnvelopeDao) : EnvelopeRepository {
  /**
   * Observes all active envelopes.
   *
   * @return A [Flow] of domain model active envelopes
   */
  override fun getActiveEnvelopes(): Flow<List<Envelope>> =
    dao.getActiveEnvelopes().map { list -> list.map { it.toDomain() } }

  /**
   * Retrieves a single envelope by ID.
   *
   * @param id The envelope ID
   * @return The domain model envelope or null if not found
   */
  override suspend fun getById(id: String): Envelope? = dao.getById(id)?.toDomain()

  /**
   * Inserts a new envelope into the database.
   *
   * @param envelope The domain model envelope to insert
   */
  override suspend fun insert(envelope: Envelope) { dao.insert(envelope.toEntity()) }

  /**
   * Updates an existing envelope in the database.
   *
   * @param envelope The domain model envelope with updated values
   */
  override suspend fun update(envelope: Envelope) { dao.update(envelope.toEntity()) }

  /**
   * Soft-deletes an envelope by marking it inactive.
   *
   * The envelope record remains in the database but is hidden from queries
   * that filter by isActive = true.
   *
   * @param id The envelope ID to soft-delete
   */
  override suspend fun softDelete(id: String) { dao.softDelete(id) }
}
