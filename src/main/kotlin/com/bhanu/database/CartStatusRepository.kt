package com.bhanu.database

import io.vertx.mutiny.sqlclient.Tuple
import io.vertx.mutiny.sqlclient.Row
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.UUID
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

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

    fun findStaleCarts(seconds: Long): Uni<List<CartStatus>> {
        val cutoff = OffsetDateTime.ofInstant(Instant.now().minusSeconds(seconds), ZoneOffset.UTC)

        return client.preparedQuery("""
            SELECT * FROM cart_status
            WHERE updated_timestamp < $1
            AND status NOT IN ('NOTIFICATION_TRIGGERED', 'PAYMENT_PENDING', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_CANCELLED', 'PAYMENT_REFUNDED')
        """).execute(Tuple.of(cutoff))
            .onItem().transform { rowSet ->
                rowSet.map { row -> row.toCartStatus() }
            }
    }

    fun update(cart: CartStatus): Uni<CartStatus> {
        val query = """
        UPDATE cart_status SET
            user_id = $1,
            session_id = $2,
            cart_items = $3,
            updated_timestamp = $4,
            status = $5,
            notification_id = $6,
            source = $7,
            experiment_variant = $8,
            expires_at = $9
        WHERE cart_id = $10
        RETURNING *
    """.trimIndent()

        val tuple = Tuple.tuple()
            .addString(cart.userId)
            .addString(cart.sessionId)
            .addString(cart.cartItems)
            .addTemporal(cart.updatedTimestamp)
            .addString(cart.status)
            .addString(cart.notificationId)
            .addString(cart.source)
            .addString(cart.experimentVariant)
            .addTemporal(cart.expiresAt)
            .addUUID(cart.cartId)

        return client.preparedQuery(query)
            .execute(tuple)
            .onItem().transform { rowSet ->
                val row = rowSet.iterator().next()
                row.toCartStatus() // assumes you have a Row mapper already
            }
    }

    fun markNotificationTriggered(cartId: UUID): Uni<CartStatus> {
        val query = """
        UPDATE cart_status
        SET status = $1, updated_timestamp = $2
        WHERE cart_id = $3
        RETURNING *
    """.trimIndent()

        val now = OffsetDateTime.now(ZoneOffset.UTC)

        val tuple = Tuple.tuple()
            .addString("NOTIFICATION_TRIGGERED")
            .addOffsetDateTime(now)
            .addUUID(cartId)

        return client.preparedQuery(query)
            .execute(tuple)
            .onItem().transform { rowSet ->
                val row = rowSet.iterator().next()
                row.toCartStatus()
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
