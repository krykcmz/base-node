package com.bitclave.node.controllers.v1

import com.bitclave.node.controllers.AbstractController
import com.bitclave.node.repository.models.UploadedFile
import com.bitclave.node.repository.models.SignedRequest
import com.bitclave.node.services.errors.BadArgumentException
import com.bitclave.node.services.errors.NotFoundException
import com.bitclave.node.services.v1.FileService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture
import javax.annotation.Resource

@RestController
@RequestMapping("/v1/file/{publicKey}")
class FileController(
        @Qualifier("v1") private val fileService: FileService
) : AbstractController() {

    /**
     * Creates new or updates a file in the system, based on the provided information.
     *
     * @return id of created/updated file. Http status - 200/201.
     *
     * @exception   {@link BadArgumentException} - 400
     *              {@link AccessDeniedException} - 403
     *              {@link DataNotSaved} - 500
     */

    @ApiOperation("Creates new or updates a file in the system, based on the provided information.",
            response = Long::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Updated", response = Long::class),
        ApiResponse(code = 201, message = "Created", response = Long::class),
        ApiResponse(code = 400, message = "BadArgumentException"),
        ApiResponse(code = 403, message = "AccessDeniedException"),
        ApiResponse(code = 500, message = "DataNotSaved")
    ])
    @RequestMapping(method = [RequestMethod.PUT], value = ["/", "{id}"])
    fun uploadFile(
            @ApiParam("where client sends MultipartFile", required = true)
            @RequestParam(value = "data", required = true)
            data: MultipartFile,

            @ApiParam("where client sends other parameters except MultipartFile data of file", required = true)
            @PathVariable(value = "publicKey", required = true)
            publicKey: String,

            @ApiParam("Optional id of already created a offer. Use for update offer", required = false)
            @PathVariable(value = "id", required = false)
            id: Long?,

            @ApiParam("change repository strategy", allowableValues = "POSTGRES", required = false)
            @RequestHeader("Strategy", required = false)
            strategy: String?): CompletableFuture<ResponseEntity<UploadedFile>> {

        if(publicKey.isEmpty() || data.isEmpty) {
            throw BadArgumentException()
        }

        return fileService.saveFile(data, publicKey, id ?: 0, getStrategyType(strategy))
                .thenCompose {
                    val status = if (it.id != id) HttpStatus.CREATED else HttpStatus.OK
                    CompletableFuture.completedFuture(ResponseEntity<UploadedFile>(it, status))
                }
    }

    /**
     * Delete a file from the system.
     * @param request is {@link File} where client sends {@link Long}
     *
     * @return {@link Long}, Http status - 200.
     *
     * @exception   {@link BadArgumentException} - 400
     *              {@link AccessDeniedException} - 403
     *              {@link DataNotSaved} - 500
     */

    @ApiOperation("Delete a file from the system.",
            response = Long::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Deleted", response = Long::class),
        ApiResponse(code = 400, message = "BadArgumentException"),
        ApiResponse(code = 403, message = "AccessDeniedException"),
        ApiResponse(code = 404, message = "NotFoundException")
    ])
    @RequestMapping(method = [RequestMethod.DELETE], value = ["{id}"])
    fun deleteFile(
            @ApiParam("public key of file", required = true)
            @PathVariable(value = "publicKey", required = true)
            publicKey: String,

            @ApiParam("id of existed file.", required = true)
            @PathVariable(value = "id", required = true)
            id: Long,

            @ApiParam("change repository strategy", allowableValues = "POSTGRES", required = false)
            @RequestHeader("Strategy", required = false)
            strategy: String?): CompletableFuture<Long> {

        return fileService.deleteFile(id,publicKey,getStrategyType(strategy))
    }

    /**
     * Download a file in the system.
     *
     * @return {@link Resource}, Http status - 200.
     */
    @ApiOperation(
            "download a file in the system", response = Resource::class
    )
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Success", response = Resource::class),
        ApiResponse(code = 404, message = "NotFoundException")
    ])
    @RequestMapping(method = [RequestMethod.GET], value = ["{id}"])
    fun downloadFile(
            @ApiParam("publicKey who create file", required = true)
            @PathVariable("publicKey", required = true)
            publicKey: String,

            @ApiParam("id of already created file.", required = true)
            @PathVariable(value = "id", required = true)
            id: Long,

            @ApiParam("change repository strategy", allowableValues = "POSTGRES", required = false)
            @RequestHeader("Strategy", required = false)
            strategy: String?): CompletableFuture<ResponseEntity<ByteArray>> {

        return fileService.getFile(id, publicKey,getStrategyType(strategy))
                .thenCompose {
                    if (it == null || it.id != id || it.data == null) throw NotFoundException()
                    CompletableFuture.completedFuture(ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + it.name + "\"")
                            .body(it.data!!))
                }
    }

}
