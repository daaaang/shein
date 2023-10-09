package com.order.domain.model

import java.util.*

data class Order(
    val userId: Long,
    val orderStatus: OrderStatus,
    val txId: String,
    val id: Long = 0L,
) {
    companion object {
        fun fromOrderRequest(orderRequest: OrderRequest): Order {
            return Order(
                userId = orderRequest.userId,
                orderStatus = OrderStatus.PENDING,
                txId = UUID.randomUUID().toString(),
            )
        }
    }
}