package hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
    fun findByIdAndAdvertiserId(id: Int, advertiserId: Int): Optional<Advertisement>
    fun findByCategoryIn(ids: List<Category>): List<Advertisement>
}