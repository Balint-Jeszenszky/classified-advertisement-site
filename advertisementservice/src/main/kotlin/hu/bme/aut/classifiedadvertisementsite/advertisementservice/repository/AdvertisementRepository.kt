package hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface AdvertisementRepository : JpaRepository<Advertisement, Int> {
    fun findByIdAndAdvertiserId(id: Int, advertiserId: Int): Optional<Advertisement>
    fun findByCategoryIn(categories: List<Category>): List<Advertisement>
    @Query("select a from Advertisement a where a.category in (:categories) and (upper(a.title) like concat('%',upper(:query),'%') or upper(a.description) like concat('%',upper(:query),'%'))")
    fun findByCategoryInAndTitleContainsOrDescriptionContains(@Param("categories") categories: List<Category>, @Param("query") query: String): List<Advertisement>
    @Query("select * from (select *, row_number() over (partition by category_id order by created_at desc) as RN from advertisement)RNK where RN <= :limit", nativeQuery = true)
    fun getNewAdvertisements(@Param("limit") limit: Int): List<Advertisement>
    fun findByTitleContainsOrDescriptionContains(title: String, description: String): List<Advertisement>
}