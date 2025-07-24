package com.bhanu.database

import notifications.Checkoutservice
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

data class CartStatus(
    val cartId: UUID,
    val userId: String?,
    val sessionId: String?,
    val cartItems: String, // JSONB as string
    val updatedTimestamp: OffsetDateTime,
    val status: String,
    val notificationId: String?,
    val source: String?,
    val experimentVariant: String?,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime?
)

fun Checkoutservice.CartEvent.toCartStatus(): CartStatus {
    val eventTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(eventTime.seconds, eventTime.nanos.toLong()), ZoneOffset.UTC)

    return CartStatus(
        cartId = UUID.fromString(cartId),
        userId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.USER_ID }?.id ?: "",
        sessionId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.SESSION_ID }?.id ?: "",
        cartItems = itemsList.toString(), // optional: serialize item list to string
        updatedTimestamp = eventTime,
        status = action.name,
        notificationId = null,
        source = source,
        experimentVariant = null,
        createdAt = eventTime,
        expiresAt = eventTime.plusWeeks(1) // default TTL (15 min), configurable
    )
}

fun Checkoutservice.CheckoutEvent.toCartStatus(): CartStatus {
    val eventTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(checkoutTime.seconds, checkoutTime.nanos.toLong()), ZoneOffset.UTC)

    return CartStatus(
        cartId = UUID.fromString(cartId),
        userId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.USER_ID }?.id ?: "",
        sessionId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.SESSION_ID }?.id ?: "",
        cartItems = "[]", // Optional: replace with reconstructed items if available
        updatedTimestamp = eventTime,
        status = paymentStatus.name, // e.g., "PAYMENT_PENDING", "PAYMENT_SUCCESS"
        notificationId = null,
        source = "", // If you capture traffic source earlier, inject it here
        experimentVariant = null,
        createdAt = eventTime,
        expiresAt = eventTime.plusWeeks(1) // Default expiry: 15 minutes from now
    )
}