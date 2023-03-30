package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.CommentApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentService: CommentService
) : ExternalApi, CommentApi {
    override fun deleteAdvertisementAdIdCommentCommentId(id: Int): ResponseEntity<Unit> {
        commentService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getAdvertisementIdComment(id: Int): ResponseEntity<List<CommentResponse>> {
        val comments = commentService.getCommentsByAdvertisementId(id)
        return ResponseEntity(comments, HttpStatus.OK)
    }

    override fun postAdvertisementIdComment(id: Int, commentRequest: CommentRequest?): ResponseEntity<CommentResponse> {
        if (commentRequest == null) {
            throw BadRequestException("Invalid data")
        }
        val comment = commentService.createComment(id, commentRequest)
        return ResponseEntity(comment, HttpStatus.CREATED)
    }
}