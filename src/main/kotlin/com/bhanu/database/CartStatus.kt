package com.bhanu.database

import java.time.OffsetDateTime
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