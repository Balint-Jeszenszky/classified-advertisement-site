package hu.bme.aut.classifiedadvertisementsite.bidservice.repository

import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BidRepository : JpaRepository<Bid, Int> {
    @Query("select b from Bid b where b.price = (select max(b2.price) from Bid b2 where b2.advertisement = b.advertisement)")
    fun findTopBidsForAdvertisementsByIds(@Param("ids") ids: List<Int>): List<Bid>
}