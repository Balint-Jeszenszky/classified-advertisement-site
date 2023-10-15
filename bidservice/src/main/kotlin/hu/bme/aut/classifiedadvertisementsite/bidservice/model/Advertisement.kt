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

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "advertisement_id")
    var bids: List<Bid> = mutableListOf(),
)
