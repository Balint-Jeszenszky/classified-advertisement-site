package hu.bme.aut.classifiedadvertisementsite.advertisementservice.unit

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.ForbiddenException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.*
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CommentRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.UserDetailsImpl
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.CommentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.OffsetDateTime
import java.util.*
import org.mockito.Mockito.`when` as mockitoWhen

class CommentServiceTest {
    private lateinit var commentRepository: CommentRepository
    private lateinit var advertisementRepository: AdvertisementRepository
    private lateinit var loggedInUserService: LoggedInUserService

    private lateinit var commentService: CommentService

    @BeforeEach
    fun init() {
        commentRepository = mock(CommentRepository::class.java)
        advertisementRepository = mock(AdvertisementRepository::class.java)
        loggedInUserService = mock(LoggedInUserService::class.java)

        commentService = CommentService(commentRepository, advertisementRepository, loggedInUserService)
    }

    @Test
    fun `get comments by advertisement id`() {
        val id = 1
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            id)
        mockitoWhen(commentRepository.findByAdvertisement_AdvertiserIdOrderByCreatedAt(id)).thenReturn(listOf(
            Comment("text", 1, advertisement, OffsetDateTime.now(), 1),
            Comment("text", 1, advertisement, OffsetDateTime.now(), 1)))

        val comments = commentService.getCommentsByAdvertisementId(id)

        verify(commentRepository).findByAdvertisement_AdvertiserIdOrderByCreatedAt(id);
        assertEquals(comments.size, 2)
    }

    @Test
    fun `get comments for not existing advertisement`() {
        val id = 1
        mockitoWhen(commentRepository.findByAdvertisement_AdvertiserIdOrderByCreatedAt(id)).thenReturn(emptyList())

        val comments = commentService.getCommentsByAdvertisementId(id)

        verify(commentRepository).findByAdvertisement_AdvertiserIdOrderByCreatedAt(id);
        assertEquals(0, comments.size)
    }

    @Test
    fun `create comment`() {
        val advertisementId = 1
        val userId = 2
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            advertisementId)
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(userId, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(advertisementRepository.findById(advertisementId)).thenReturn(Optional.of(advertisement))
        mockitoWhen(commentRepository.save(any(Comment::class.java))).thenAnswer { it.getArguments()[0] }

        val comment = commentService.createComment(advertisementId, CommentRequest("comment text"))

        verify(loggedInUserService).getLoggedInUser()
        verify(advertisementRepository).findById(advertisementId)
        verify(commentRepository).save(any(Comment::class.java))
        assertEquals(userId, comment.creatorId)
    }

    @Test
    fun `create comment no logged in user`() {
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(null)

        assertThrows<ForbiddenException> { commentService.createComment(1, CommentRequest("comment text")) }

        verify(loggedInUserService).getLoggedInUser()
        verify(advertisementRepository, never()).findById(anyInt())
        verify(commentRepository, never()).save(any(Comment::class.java))
    }

    @Test
    fun `create comment advertisement not found`() {
        val advertisementId = 1
        val userId = 2
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(userId, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(advertisementRepository.findById(advertisementId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { commentService.createComment(1, CommentRequest("comment text")) }

        verify(loggedInUserService).getLoggedInUser()
        verify(advertisementRepository).findById(advertisementId)
        verify(commentRepository, never()).save(any(Comment::class.java))
    }

    @Test
    fun `delete comment`() {
        val advertisementId = 1
        val commentId = 2
        val userId = 3
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            advertisementId)
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(userId, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(loggedInUserService.isAdmin()).thenReturn(false)
        mockitoWhen(commentRepository.findById(commentId)).thenReturn(Optional.of(
            Comment("text", userId, advertisement, OffsetDateTime.now(), commentId)))

        commentService.deleteById(commentId)

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService).isAdmin()
        verify(commentRepository).findById(commentId)
        verify(commentRepository).delete(any(Comment::class.java))
    }

    @Test
    fun `delete other user's comment admin`() {
        val advertisementId = 1
        val commentId = 2
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            advertisementId)
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(3, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(loggedInUserService.isAdmin()).thenReturn(true)
        mockitoWhen(commentRepository.findById(commentId)).thenReturn(Optional.of(
            Comment("text", 4, advertisement, OffsetDateTime.now(), commentId)))

        commentService.deleteById(commentId)

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService).isAdmin()
        verify(commentRepository).findById(commentId)
        verify(commentRepository).delete(any(Comment::class.java))
    }

    @Test
    fun `delete other user's comment not admin`() {
        val advertisementId = 1
        val commentId = 2
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            advertisementId)
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(3, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(loggedInUserService.isAdmin()).thenReturn(false)
        mockitoWhen(commentRepository.findById(commentId)).thenReturn(Optional.of(
            Comment("text", 4, advertisement, OffsetDateTime.now(), commentId)))

        assertThrows<ForbiddenException> { commentService.deleteById(commentId) }

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService).isAdmin()
        verify(commentRepository).findById(commentId)
        verify(commentRepository, never()).delete(any(Comment::class.java))
    }

    @Test
    fun `delete comment user not logged in`() {
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(null)

        assertThrows<ForbiddenException> { commentService.deleteById(1) }

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService, never()).isAdmin()
        verify(commentRepository, never()).findById(anyInt())
        verify(commentRepository, never()).delete(any(Comment::class.java))
    }

    @Test
    fun `delete not existing comment`() {
        val advertisementId = 1
        val commentId = 2

        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(3, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(commentRepository.findById(commentId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { commentService.deleteById(commentId) }

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService, never()).isAdmin()
        verify(commentRepository).findById(commentId)
        verify(commentRepository, never()).delete(any(Comment::class.java))
    }
}