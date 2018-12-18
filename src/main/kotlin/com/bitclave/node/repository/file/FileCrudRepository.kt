package com.bitclave.node.repository.file

import com.bitclave.node.repository.models.File
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
interface FileCrudRepository : CrudRepository<File, String> {

    fun findByPublicKey(publicKey: String): List<File>

    fun findById(id: Long): File?

    fun deleteByIdAndPublicKey(id: Long, publicKey: String): Long

}
