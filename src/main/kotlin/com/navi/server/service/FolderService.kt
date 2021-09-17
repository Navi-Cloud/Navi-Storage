package com.navi.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.navi.server.domain.FileObject
import com.navi.server.domain.GridFSRepository
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream
import java.util.*

@GrpcService
class FolderService {

    @Autowired
    private lateinit var gridFSRepository: GridFSRepository

    fun createRootFolder(createRootFolderRequest: navistorage.StorageMessage.CreateRootFolderRequest,
                         responseObserver: StreamObserver<CommonCommunication.Result>)
    {
        gridFSRepository.saveToGridFS(
            fileObject = FileObject(
                userEmail = createRootFolderRequest.userEmail,
                category = "",
                fileName = "/",
                currFolderName = "",
                lastModifiedTime = Date().toString(),
                isFile = false,
                isFavorites = false,
                isTrash = false
            ),
            inputStream = ByteArrayInputStream("".toByteArray())
        )

        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }

    fun findInsideFiles(findInsideFilesRequest: navistorage.StorageMessage.FindInsideFilesRequest,
                        responseObserver: StreamObserver<CommonCommunication.Result>)
    {
        val filesList: List<FileObject> = gridFSRepository.getMetadataInsideFolder(
            userEmail = findInsideFilesRequest.userEmail,
            targetFolderName = findInsideFilesRequest.targetFolder
        )
        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .setObject(ObjectMapper().writeValueAsString(filesList))
            .build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}