package com.order.application.service

import com.order.application.port.OrderProductQueryPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.OrderKitchenTicketStatusUpdatePublishEvent
import com.order.domain.model.ProductItem
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderKitchenService(
    private val orderQueryPort: OrderQueryPort,
    private val orderProductQueryPort: OrderProductQueryPort,
) : OrderKitchenUseCase {

    @Transactional(readOnly = true)
    override fun createOrderKitchenEvent(txId: String): OrderKitchenCreationPublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)
        val orderProducts = orderProductQueryPort.getOrderProductByOrderId(order.id)

        return OrderKitchenCreationPublishEvent(
            txId = txId,
            orderId = order.id,
            productItems = orderProducts.map {
                ProductItem(
                    productId = it.productId,
                    amount = it.amount,
                )
            },
        )
    }

    override fun approvalOrderKitchenEvent(txId: String): OrderKitchenTicketStatusUpdatePublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenTicketStatusUpdatePublishEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.APPROVAL,
        )
    }

    override fun rejectOrderKitchenEvent(txId: String): OrderKitchenTicketStatusUpdatePublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenTicketStatusUpdatePublishEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.REJECT,
        )
    }
}