package hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import org.springframework.data.jpa.repository.JpaRepository

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
}