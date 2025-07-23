package com.bhanu.checkout

import com.bhanu.purchaseintent.EmitterService
import io.quarkus.grpc.GrpcService
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import notifications.CheckoutService
import notifications.Checkoutservice
import notifications.Checkoutservice.CartEvent
import notifications.Checkoutservice.CreateOrUpdateCartResponse
import notifications.Checkoutservice.PurchaseIntentEvent
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter

@GrpcService
class CheckoutServiceImpl: CheckoutService {

    @Inject
    private lateinit var purchaseIntentEmitter: EmitterService

    override fun createOrUpdateCart(request: Checkoutservice.CartEvent?): Uni<Checkoutservice.CreateOrUpdateCartResponse> {
        if (request != null) {
            purchaseIntentEmitter.emit(PurchaseIntentEvent.newBuilder()
                .setCartEvent(
                    CartEvent.newBuilder()
                        .setCartId(request.cartId)
                        .build()
                )
                .build()
                .toString())
        }
        val response = CreateOrUpdateCartResponse.newBuilder()
            .setSuccess(true)
            .setMessage("The request reached the server")
            .build()
        return Uni.createFrom().item(response)
    }

    override fun createOrUpateCheckout(request: Checkoutservice.CheckoutEvent?): Uni<Checkoutservice.CheckoutResponse> {
        TODO("Not yet implemented")
    }
}