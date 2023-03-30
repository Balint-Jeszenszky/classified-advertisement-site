package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Comment
import org.mapstruct.Mapper

@Mapper
interface CommentMapper {
    fun commentToCommentResponse(comment: Comment): CommentResponse
}