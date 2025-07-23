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
        repository.findStaleCarts(seconds = 10)
            .subscribe().with(
                { staleCarts ->
                    if (staleCarts.isNotEmpty()) {
                        println("🔍 Found ${staleCarts.size} carts stale for > 10s:")
                        staleCarts.forEach { println("🛒 Cart ${it.cartId} last updated at ${it.updatedTimestamp}") }
                    }
                },
                { error -> println("❌ Failed to sweep carts: ${error.message}") }
            )
    }
}
