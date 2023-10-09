package com.order.domain.model

data class OrderRequest(
    val userId: Long,
    val orderProducts: List<OrderProduct>,
)
