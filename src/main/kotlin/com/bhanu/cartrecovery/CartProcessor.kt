package com.bhanu.cartrecovery

import com.bhanu.database.CartStatus
import com.bhanu.database.CartStatusRepository
import com.bhanu.database.toCartStatus
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import notifications.Checkoutservice.CartAction
import notifications.Checkoutservice.PurchaseIntentEvent
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.time.OffsetDateTime
import java.util.*

@ApplicationScoped
class CartProcessor {

    @Inject
    lateinit var repository: CartStatusRepository

    @Incoming("purchase-intent")
    fun consume(intentMessage: ByteArray) {
        val purchaseIntent = PurchaseIntentEvent.parseFrom(intentMessage)
        if (purchaseIntent.hasCartEvent()) {

        } else if (purchaseIntent.hasCheckoutEvent()) {

        } else {

        }
        val cartId = UUID.randomUUID()
        repository.insert(
            CartStatus(
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
            )
        ).subscribe().with(
            { inserted -> println("✅ Inserted CartStatus: $inserted") },
            { error -> println("❌ Failed to insert: ${error.message}") }
        )
    }

    fun handlePurchaseIntentEvent(event: PurchaseIntentEvent) {
        when {
            event.hasCartEvent() -> {
                upsertCartStatus(event.cartEvent.toCartStatus())
            }
            event.hasCheckoutEvent() -> {

            }

            else -> throw IllegalArgumentException("❌ Unknown PurchaseIntent Event Received")
        }
    }

    fun upsertCartStatus(cart: CartStatus) {
        repository.findByCartId(cart.cartId)
            .onItem().transformToUni { existing ->
                if (existing != null) {
                    val updatedCart = existing.copy(
                        cartItems = cart.cartItems,
                        updatedTimestamp = cart.updatedTimestamp,
                        status = cart.status,
                        notificationId = cart.notificationId,
                        experimentVariant = cart.experimentVariant,
                        source = cart.source,
                        expiresAt = cart.expiresAt
                    )
                    repository.update(updatedCart)
                } else {
                    repository.insert(cart)
                }
            }
            .subscribe().with(
                { result -> println("✅ Cart upserted: ${result.cartId}") },
                { error -> println("❌ Failed to upsert cart: ${error.message}") }
            )
    }
}
