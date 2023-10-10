package com.order.domain.model.kitchen

import com.order.domain.model.ProductPrice

data class KitchenTicket(
    val txId: String,
    val orderId: Long,
    val productPrices: List<ProductPrice>,
)