package com.order.domain.model

data class OrderProduct(
    val id: Long = 0L,
    val orderId: Long,
    val productId: Long,
    val amount: Long,
)
