package hu.bme.aut.classifiedadvertisementsite.advertisementservice.model

enum class AdvertisementStatus(val value: String) {
    AVAILABLE("AVAILABLE"),
    FREEZED("FREEZED"),
    SOLD("SOLD"),
    ARCHIVED("ARCHIVED")
}