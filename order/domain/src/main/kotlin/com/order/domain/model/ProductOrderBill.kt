package com.order.domain.model

data class ProductOrderBill(
    val txId: String,
    val orderId: Long,
    val productItems: List<ProductItem>,
)