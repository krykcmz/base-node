package com.bitclave.node.repository.search

import com.bitclave.node.repository.models.SearchRequest
import com.bitclave.node.services.errors.DataNotSaved
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("postgres")
class PostgresSearchRequestRepositoryImpl(
        val repository: SearchRequestCrudRepository
) : SearchRequestRepository {

    override fun saveSearchRequest(request: SearchRequest): SearchRequest {
        return repository.save(request) ?: throw DataNotSaved()
    }

    override fun deleteSearchRequest(id: Long, owner: String): Long {
        val count = repository.deleteByIdAndOwner(id, owner)
        if (count > 0) {
            return id
        }

        return 0
    }

    override fun findByOwner(owner: String): List<SearchRequest> {
        return repository.findByOwner(owner)
    }

    override fun findByIdAndOwner(id: Long, owner: String): SearchRequest? {
        return repository.findByIdAndOwner(id, owner)
    }

}