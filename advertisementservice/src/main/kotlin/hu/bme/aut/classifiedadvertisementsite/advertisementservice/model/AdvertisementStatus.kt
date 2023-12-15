package hu.bme.aut.classifiedadvertisementsite.advertisementservice.model

enum class AdvertisementStatus(val value: String) {
    AVAILABLE("AVAILABLE"),
    FREEZED("FREEZED"),
    BIDDING("BIDDING"),
    SOLD("SOLD"),
    ARCHIVED("ARCHIVED")
}