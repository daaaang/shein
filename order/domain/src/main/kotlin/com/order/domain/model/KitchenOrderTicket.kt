package com.order.domain.model

sealed class KitchenOrder

data class KitchenOrderTicket(
    val txId: String,
    val orderId: Long,
    val kitchenStatus: KitchenStatusType,
): KitchenOrder()