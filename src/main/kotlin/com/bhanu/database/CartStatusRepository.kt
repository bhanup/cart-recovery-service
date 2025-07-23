package com.bhanu.database

import io.vertx.mutiny.sqlclient.Tuple
import io.vertx.mutiny.sqlclient.Row
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.UUID
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import java.time.OffsetDateTime

@ApplicationScoped
class CartStatusRepository {

    @Inject
    lateinit var client: Pool

    fun insert(cart: CartStatus): Uni<CartStatus> {
        val query = """
            INSERT INTO cart_status (
                cart_id, user_id, session_id, cart_items,
                updated_timestamp, status, notification_id,
                source, experiment_variant, created_at, expires_at
            ) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11)
            RETURNING *
        """

        val tuple = Tuple.tuple()
            .addUUID(cart.cartId)
            .addString(cart.userId)
            .addString(cart.sessionId)
            .addString(cart.cartItems)
            .addTemporal(cart.updatedTimestamp)           // Instant
            .addString(cart.status)
            .addString(cart.notificationId)
            .addString(cart.source)
            .addString(cart.experimentVariant)
            .addTemporal(cart.createdAt)                  // Instant
            .addTemporal(cart.expiresAt)                  // Instant or null

        return client.preparedQuery(query)
            .execute(tuple)
            .onItem().transform { rowSet ->
                if (rowSet.iterator().hasNext())
                    rowSet.iterator().next().toCartStatus()
                else null
            }

    }

    fun findByCartId(cartId: UUID): Uni<CartStatus?> {
        val query = "SELECT * FROM cart_status WHERE cart_id = $1"

        return client.preparedQuery(query)
            .execute(Tuple.of(cartId))
            .onItem().transform { rowSet ->
                if (rowSet.iterator().hasNext()) {
                    rowSet.iterator().next().toCartStatus()
                } else null
            }
    }

    private fun Row.toCartStatus(): CartStatus = CartStatus(
        cartId = getUUID("cart_id"),
        userId = getString("user_id"),
        sessionId = getString("session_id"),
        cartItems = getString("cart_items"),
        updatedTimestamp = get(OffsetDateTime::class.java, "updated_timestamp"),
        status = getString("status"),
        notificationId = getString("notification_id"),
        source = getString("source"),
        experimentVariant = getString("experiment_variant"),
        createdAt = get(OffsetDateTime::class.java, "created_at"),
        expiresAt = get(OffsetDateTime::class.java, "expires_at")
    )
}