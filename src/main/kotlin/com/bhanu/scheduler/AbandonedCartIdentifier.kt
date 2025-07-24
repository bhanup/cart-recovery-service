package com.bhanu.scheduler

import com.bhanu.database.CartStatusRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class AbandonedCartIdentifier {

    @Inject
    lateinit var repository: CartStatusRepository

    @Scheduled(every = "10s")
    fun sweepInactiveCarts() {
        repository.findStaleCarts(seconds = 60)
            .subscribe().with(
                { staleCarts ->
                    if (staleCarts.isNotEmpty()) {
                        println("🔍 Found ${staleCarts.size} carts stale for > 10s:")
                        staleCarts.forEach {
                            println("🛒 Cart ${it.cartId} last updated at ${it.updatedTimestamp}")
                            println(it)
                            repository.markNotificationTriggered(it.cartId)
                                .subscribe().with(
                                    { result -> println("✅ Cart upserted: ${result.cartId}: ${result}") },
                                    { error -> println("❌ Failed to upsert cart: ${error.message}") }
                                )
                        }
                    }
                },
                { error -> println("❌ Failed to sweep carts: ${error.message}") }
            )
    }
}
