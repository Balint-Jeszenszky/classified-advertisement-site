package hu.bme.aut.classifiedadvertisementsite.advertisementservice.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity(name = "Comment")
@Table(name = "comment")
class Comment (

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "creator_id", nullable = false, updatable = false)
    var creatorId: Int,

    @ManyToOne
    @JoinColumn(name = "advertisement_id", nullable = false)
    var advertisement: Advertisement,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: Int? = null
)