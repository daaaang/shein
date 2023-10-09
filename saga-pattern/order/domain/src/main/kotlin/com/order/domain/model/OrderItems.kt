package com.order.domain.model

data class OrderItems(
    val orderId: Long,
    val orderProducts: List<OrderProduct>,
)