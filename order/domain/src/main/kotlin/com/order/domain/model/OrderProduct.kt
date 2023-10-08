package com.order.domain.model

data class OrderProduct(
    val orderId: Long,
    val productItems: List<ProductItem>,
)