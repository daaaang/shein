package com.order.domain.events

import com.order.domain.model.OrderProduct
import com.order.domain.model.Payment

sealed class Event(
    open val txId: String,
)

data class OrderUserEvent(
    override val txId: String,
    val userId: Long,
) : Event(txId = txId)

data class OrderKitchenEvent(
    override val txId: String,
    val orderId: Long,
    val orderProducts: List<OrderProduct>,
) : Event(txId = txId)

data class OrderPaymentEvent(
    override val txId: String,
    val orderId: Long,
    val payment: Payment,
) : Event(txId = txId)