package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub

import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class Util {
    companion object {
        fun <T> deserialize(message: ByteArray): T? {
            return try {
                ObjectInputStream(ByteArrayInputStream(message)).readObject() as T
            } catch (e: Exception) {
                null
            }
        }
    }
}