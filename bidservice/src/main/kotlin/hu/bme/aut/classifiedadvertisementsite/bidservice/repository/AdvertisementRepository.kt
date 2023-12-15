package hu.bme.aut.classifiedadvertisementsite.bidservice.repository

import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
    fun findAllByExpirationBeforeAndArchivedIsFalseAndNotifiedIsFalse(expiration: OffsetDateTime): List<Advertisement>
}