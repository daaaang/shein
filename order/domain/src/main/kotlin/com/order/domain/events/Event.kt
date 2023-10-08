package com.order.domain.events

import com.order.domain.model.OrderProduct
import com.order.domain.model.KitchenOrderTicket
import com.order.domain.model.Order

sealed class Event(
    open val txId: String,
)

data class OrderProductEvent(
    override val txId: String,
    val orderId: Long,
    val orderProducts: List<OrderProduct>,
) : Event(txId = txId)

data class OrderKitchenEvent(
    override val txId: String,
    val orderId: Long,
    val orderProducts: List<OrderProduct>,
) : Event(txId = txId)

data class OrderPaymentEvent(
    override val txId: String,
    val orderId: Long,

) : Event(txId = txId)