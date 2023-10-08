package com.order.domain.events

import com.order.domain.model.ProductItem
import com.order.domain.model.ProductOrderBill

sealed class Event(
    open val txId: String,
)

data class OrderProductEvent(
    override val txId: String,
    val orderId: Long,
    val productItems: List<ProductItem>,
) : Event(txId = txId)

data class OrderKitchenEvent(
    override val txId: String,
    val orderId: Long,
    val productItems: List<ProductItem>,
) : Event(txId = txId) {

    companion object {
        fun fromOrderProductBill(orderProductOrderBill: ProductOrderBill): OrderKitchenEvent {
            return OrderKitchenEvent(
                txId = orderProductOrderBill.txId,
                orderId = orderProductOrderBill.orderId,
                productItems = orderProductOrderBill.productItems,
            )
        }
    }
}