package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.CommentApi
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController : ExternalApi, CommentApi {
}