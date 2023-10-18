package hu.bme.aut.classifiedadvertisementsite.bidservice.model

import jakarta.persistence.*

@Entity(name = "Bid")
@Table(name = "bid")
class Bid (

    @Column(name = "user_id", updatable = false, nullable = false)
    var userId: Int,

    @Column(name = "price", updatable = false, nullable = false)
    var price: Double,

    @ManyToOne
    @JoinColumn(name = "advertisement_id", nullable = false)
    var advertisement: Advertisement,

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_seq")
    @SequenceGenerator(name = "bid_seq", sequenceName = "bid_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: Int? = null
)
