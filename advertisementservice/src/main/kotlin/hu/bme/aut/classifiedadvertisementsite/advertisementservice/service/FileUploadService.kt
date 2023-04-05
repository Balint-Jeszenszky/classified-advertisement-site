package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import io.minio.*
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileUploadService(
    @Value("\${minio.endpoint}") private val endpoint: String,
    @Value("\${minio.username}") private val username: String,
    @Value("\${minio.password}") private val password: String,
    @Value("\${minio.bucket}") private val bucket: String
) {

    private val minioClient: MinioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(username, password)
        .build()

    private fun createBucketIfNotExists() {
        val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
        }
    }

    fun uploadFile(file: Resource, advertisementId: Int) {
        createBucketIfNotExists()
        val uuid = UUID.randomUUID().toString()
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucket)
            .`object`("raw/$uuid.${FilenameUtils.getExtension(file.filename)}")
            .stream(file.inputStream, file.contentLength(), -1)
            .build())
    }
}