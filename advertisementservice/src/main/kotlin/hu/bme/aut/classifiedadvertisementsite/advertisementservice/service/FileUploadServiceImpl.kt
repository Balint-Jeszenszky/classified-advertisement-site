package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.minio.*
import org.apache.commons.io.FilenameUtils
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Profile("!test")
class FileUploadServiceImpl(
    @Value("\${minio.endpoint}") private val endpoint: String,
    @Value("\${minio.username}") private val username: String,
    @Value("\${minio.password}") private val password: String,
    @Value("\${minio.bucket}") private val bucket: String,
    private val rabbitTemplate: RabbitTemplate,
    private val queue: Queue
) : FileUploadService {

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

    override fun uploadFiles(files: MutableList<MultipartFile>, advertisementId: Int) {
        createBucketIfNotExists()
        files.forEach {
            if (!listOf("image/jpeg", "image/png").contains(it.contentType)) {
                return
            }
            val name = "${UUID.randomUUID()}.${FilenameUtils.getExtension(it.originalFilename)}"
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .`object`("raw/$name")
                .stream(it.inputStream, it.size, -1)
                .build())
            sendImageProcessingMessage(name, advertisementId)
        }
    }

    override fun deleteImagesForAd(advertisementId: Int) {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()

        node.put("type", "DELETE")
        node.put("advertisementId", advertisementId)
        rabbitTemplate.convertAndSend(queue.name, node.toString())
    }

    private fun sendImageProcessingMessage(name: String, advertisementId: Int) {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()

        node.put("type", "PROCESS")
        node.put("name", name)
        node.put("advertisementId", advertisementId)
        rabbitTemplate.convertAndSend(queue.name, node.toString())
    }

    override fun deleteImagesById(deletedImages: MutableList<Int>) {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()

        node.put("type", "DELETE")
        val arrayNode = mapper.createArrayNode()
        deletedImages.forEach(arrayNode::add)
        node.set<JsonNode>("imageIds", arrayNode)
        rabbitTemplate.convertAndSend(queue.name, node.toString())
    }
}