package hu.bme.aut.classifiedadvertisementsite.bidservice.repository

import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<Bid, Int> {
}