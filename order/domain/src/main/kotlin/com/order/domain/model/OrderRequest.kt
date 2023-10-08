package com.order.domain.model

data class OrderRequest(
    val userId: Long,
    val productItems: List<ProductItem>,
)
