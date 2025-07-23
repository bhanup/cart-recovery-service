package com.bhanu.cartrecovery

import com.bhanu.database.CartStatus
import com.bhanu.database.CartStatusRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import notifications.Checkoutservice.CartAction
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@ApplicationScoped
class CartProcessor {

    @Inject
    lateinit var repository: CartStatusRepository

    @Incoming("purchase-intent")
    fun consume(intentMessage: String) {
        val cartId = UUID.randomUUID()
        repository.insert(CartStatus(
            cartId = cartId,
            cartItems = "",
            userId = null,
            sessionId = null,
            updatedTimestamp = OffsetDateTime.now(),
            status = CartAction.ITEM_ADDED.toString(),
            notificationId = null,
            source = null,
            experimentVariant = null,
            createdAt = OffsetDateTime.now(),
            expiresAt = null,
        )).subscribe().with(
            { inserted -> println("✅ Inserted CartStatus: $inserted") },
            { error -> println("❌ Failed to insert: ${error.message}") }
        )
    }

}