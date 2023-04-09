package hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
    fun findByIdAndAdvertiserId(id: Int, advertiserId: Int): Optional<Advertisement>
    fun findByCategoryIn(categories: List<Category>): List<Advertisement>
    @Query("select a from Advertisement a where a.category in (:categories) and (upper(a.title) like concat('%',upper(:query),'%') or upper(a.description) like concat('%',upper(:query),'%'))")
    fun findByCategoryInAndTitleContainsOrDescriptionContains(categories: List<Category>, query: String): List<Advertisement>
}