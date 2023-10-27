package com.example.demo.domain

data class Order(
    val paymentInfo: PaymentInfo,
    val restaurantId: Long,
    val userId: Long,
    val id: Long = 0L,
) {

    fun withUpdatedPaymentInfo(paymentInfo: PaymentInfo): Order {
        return this.copy(paymentInfo = paymentInfo)
    }

    companion object {
        fun of(paymentInfo: PaymentInfo, restaurantId: Long, userId: Long, id: Long): Order {
            return Order(
                paymentInfo = paymentInfo,
                restaurantId = restaurantId,
                userId = userId,
                id = id,
            )
        }
    }

}