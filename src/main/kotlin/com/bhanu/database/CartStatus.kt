package com.bhanu.database

import io.vertx.core.json.Json
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
    val now = OffsetDateTime.now()

    return CartStatus(
        cartId = UUID.fromString(cartId),
        userId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.USER_ID }?.id ?: "",
        sessionId = user.takeIf { it.type == Checkoutservice.UserIdentifier.IdentifierType.SESSION_ID }?.id ?: "",
        cartItems = Json.encode(itemsList), // optional: serialize item list to string
        updatedTimestamp = OffsetDateTime.ofInstant(Instant.ofEpochSecond(eventTime.seconds, eventTime.nanos.toLong()), ZoneOffset.UTC),
        status = action.name,
        notificationId = null,
        source = source,
        experimentVariant = null,
        createdAt = now,
        expiresAt = now.plusSeconds(900) // default TTL (15 min), configurable
    )
}