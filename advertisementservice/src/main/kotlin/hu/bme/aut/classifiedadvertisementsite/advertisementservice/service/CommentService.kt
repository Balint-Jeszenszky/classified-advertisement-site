package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.ForbiddenException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.CommentMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Comment
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CommentRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val advertisementRepository: AdvertisementRepository,
    private val loggedInUserService: LoggedInUserService
) {
    private val commentMapper: CommentMapper = Mappers.getMapper(CommentMapper::class.java)

    fun getCommentsByAdvertisementId(id: Int): List<CommentResponse> {
        val comments = commentRepository.findByAdvertisement_AdvertiserIdOrderByCreatedAt(id)
        return comments.map { commentMapper.commentToCommentResponse(it) }
    }

    fun createComment(id: Int, commentRequest: CommentRequest): CommentResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findById(id).orElseThrow { NotFoundException("Advertisement not found") }
        val comment = Comment(commentRequest.content, user.getId(), advertisement)
        commentRepository.save(comment)
        return commentMapper.commentToCommentResponse(comment)
    }

    fun deleteById(id: Int) {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val comment = commentRepository.findById(id).orElseThrow { NotFoundException("Comment not found") }

        if (!loggedInUserService.isAdmin() && comment.creatorId != user.getId()) {
            throw ForbiddenException("Can not delete comment")
        }

        commentRepository.delete(comment)
    }
}