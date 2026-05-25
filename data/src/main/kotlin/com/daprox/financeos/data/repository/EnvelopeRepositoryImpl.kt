package com.daprox.financeos.data.repository

import com.daprox.financeos.data.db.dao.EnvelopeDao
import com.daprox.financeos.data.mapper.toDomain
import com.daprox.financeos.data.mapper.toEntity
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.repository.EnvelopeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnvelopeRepositoryImpl(private val dao: EnvelopeDao) : EnvelopeRepository {
    override fun getActiveEnvelopes(): Flow<List<Envelope>> =
        dao.getActiveEnvelopes().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Envelope? = dao.getById(id)?.toDomain()

    override suspend fun insert(envelope: Envelope) { dao.insert(envelope.toEntity()) }

    override suspend fun update(envelope: Envelope) { dao.update(envelope.toEntity()) }

    override suspend fun softDelete(id: String) { dao.softDelete(id) }
}
