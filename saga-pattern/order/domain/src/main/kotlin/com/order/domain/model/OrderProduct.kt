package com.order.domain.model

data class OrderProduct(
    val orderId: Long,
    val productId: Long,
    val amount: Long,
)
