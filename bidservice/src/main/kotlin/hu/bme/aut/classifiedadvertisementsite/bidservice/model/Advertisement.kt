package hu.bme.aut.classifiedadvertisementsite.bidservice.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity(name = "Advertisement")
@Table(name = "advertisement")
class Advertisement (
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: Int,

    @Column(name = "user_id", updatable = false, nullable = false)
    var userId: Int,

    @Column(name = "expiration", updatable = false, nullable = false)
    var expiration: OffsetDateTime,

    @Column(name = "initial_price", updatable = false, nullable = false)
    var initialPrice: Double,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "archived", nullable = false)
    var archived: Boolean = false,

    @Column(name = "notified", nullable = false)
    var notified: Boolean = false,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "advertisement")
    var bids: List<Bid> = mutableListOf(),
)
