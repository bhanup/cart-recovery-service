package com.bhanu.checkout

import com.bhanu.purchaseintent.EmitterService
import io.quarkus.grpc.GrpcService
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import notifications.CheckoutService
import notifications.Checkoutservice
import notifications.Checkoutservice.CreateOrUpdateCartResponse
import notifications.Checkoutservice.PurchaseIntentEvent

@GrpcService
class CheckoutServiceImpl: CheckoutService {

    @Inject
    private lateinit var purchaseIntentEmitter: EmitterService

    override fun createOrUpdateCart(request: Checkoutservice.CartEvent?): Uni<CreateOrUpdateCartResponse> {
        if (request != null) {
            purchaseIntentEmitter.emit(PurchaseIntentEvent.newBuilder()
                .setCartEvent(
                    request.toBuilder().build()
                )
                .build().toByteArray())
        }
        val response = CreateOrUpdateCartResponse.newBuilder()
            .setSuccess(true)
            .setMessage("The request reached the server")
            .build()
        return Uni.createFrom().item(response)
    }

    override fun createOrUpateCheckout(request: Checkoutservice.CheckoutEvent?): Uni<Checkoutservice.CheckoutResponse> {
        if (request != null) {
            purchaseIntentEmitter.emit(PurchaseIntentEvent.newBuilder()
                .setCheckoutEvent(request.toBuilder().build())
                .build()
                .toByteArray())
        }
        val response = Checkoutservice.CheckoutResponse.newBuilder()
            .setSuccess(true)
            .setMessage("The request reached the server")
            .build()
        return Uni.createFrom().item(response)
    }
}