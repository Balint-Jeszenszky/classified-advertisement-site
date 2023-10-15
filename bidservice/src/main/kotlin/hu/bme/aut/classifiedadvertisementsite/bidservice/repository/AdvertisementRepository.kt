package hu.bme.aut.classifiedadvertisementsite.bidservice.repository

import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import org.springframework.data.jpa.repository.JpaRepository

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
}