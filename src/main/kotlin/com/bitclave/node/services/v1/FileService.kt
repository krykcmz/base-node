package com.bitclave.node.services.v1

import com.bitclave.node.extensions.validateSig
import com.bitclave.node.repository.RepositoryStrategy
import com.bitclave.node.repository.RepositoryStrategyType
import com.bitclave.node.repository.file.FileRepository
import com.bitclave.node.repository.models.File
import com.bitclave.node.repository.models.SignedRequest
import com.bitclave.node.services.errors.AccessDeniedException
import com.bitclave.node.services.errors.AlreadyRegisteredException
import com.bitclave.node.services.errors.BadArgumentException
import com.bitclave.node.services.errors.NotFoundException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
@Qualifier("v1")
class FileService(private val fileRepository: RepositoryStrategy<FileRepository>) {

    fun getFile(id: Long, strategy: RepositoryStrategyType): CompletableFuture<File> {
        return CompletableFuture.supplyAsync({
            fileRepository.changeStrategy(strategy)
                    .findById(id)
        })
    }

    fun saveFile(
            file: File,
            strategy: RepositoryStrategyType
    ): CompletableFuture<File> {

        return CompletableFuture.supplyAsync({
            fileRepository.changeStrategy(strategy)
                    .saveFile(file)

            file
        })
    }

    fun deleteFile(
            id: Long,
            publicKey: String,
            strategy: RepositoryStrategyType
    ): CompletableFuture<Void> {

        return CompletableFuture.runAsync({
            fileRepository.changeStrategy(strategy)
                    .deleteFile(id, publicKey)
        })
    }

    fun getUserFiles(
            publicKey: String,
            strategy: RepositoryStrategyType
    ): CompletableFuture<List<File>> {

        return CompletableFuture.supplyAsync({
            fileRepository.changeStrategy(strategy)
                    .findByPublicKey(publicKey)
        })
    }

}
