package com.bhanu.purchaseintent

import io.smallrye.reactive.messaging.annotations.Broadcast
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter

@ApplicationScoped
class EmitterService {

    @Inject
    @Channel("purchase-intent")
    @Broadcast
    lateinit var emitter: Emitter<String>

    fun emit(message: String) {
        emitter.send(message)
    }
}