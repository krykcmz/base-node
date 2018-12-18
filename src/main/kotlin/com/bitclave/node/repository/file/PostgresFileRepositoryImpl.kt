package com.bitclave.node.repository.file

import com.bitclave.node.repository.models.File
import com.bitclave.node.services.errors.DataNotSavedException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("postgres")
class PostgresFileRepositoryImpl(
        val repository: FileCrudRepository
) : FileRepository {

    override fun findById(id: Long): File? {
        return repository.findById(id)
    }

    override fun saveFile(file: File): File {
        return repository.save(file) ?: throw DataNotSavedException()
    }

    override fun deleteFile(id: Long, publicKey: String): Long {
        val count = repository.deleteByIdAndPublicKey(id, publicKey)
        if (count > 0) {
            return id
        }

        return 0
    }

    override fun findByPublicKey(publicKey: String): List<File> {
        return repository.findByPublicKey(publicKey)
    }
}
