package hu.bme.aut.classifiedadvertisementsite.advertisementservice.model

import jakarta.persistence.*

@Entity(name = "Category")
@Table(name = "category")
class Category(

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToOne
    var parentCategory: Category?,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "parent_category_id")
    var childrenCategory: List<Category> = mutableListOf(),

    @OneToMany
    @JoinColumn(name = "category_id")
    var advertisements: List<Advertisement> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    var id: Int? = null
)