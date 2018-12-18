package com.bitclave.node.repository.file

import com.bitclave.node.repository.models.File

interface FileRepository {

    fun findById(id: Long): File?

    fun saveFile(file: File): File

    fun deleteFile(id: Long, publicKey: String): Long

    fun findByPublicKey(publicKey: String): List<File>

}
