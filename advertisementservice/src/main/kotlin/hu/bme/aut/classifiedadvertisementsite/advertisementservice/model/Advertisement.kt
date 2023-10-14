package hu.bme.aut.classifiedadvertisementsite.advertisementservice.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity(name = "Advertisement")
@Table(name = "advertisement")
class Advertisement (
    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "advertiser_id", nullable = false, updatable = false)
    var advertiserId: Int,

    @Column(name = "price", nullable = false)
    var price: Double,

    @ManyToOne
    var category: Category,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    var status: AdvertisementStatus,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    var type: AdvertisementType,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "advertisement_id")
    var comments: List<Comment> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advertisement_seq")
    @SequenceGenerator(name = "advertisement_seq", sequenceName = "advertisement_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: Int? = null
)