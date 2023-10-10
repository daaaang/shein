package com.order.application.service

import com.order.application.port.OrderProductQueryPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.OrderKitchenStatusUpdatePublishEvent
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import org.springframework.stereotype.Service

@Service
class OrderKitchenService(
    private val orderQueryPort: OrderQueryPort,
    private val orderProductQueryPort: OrderProductQueryPort,
) : OrderKitchenUseCase {

    override fun createOrderKitchenEvent(txId: String): OrderKitchenCreationPublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)
        val orderProducts = orderProductQueryPort.getOrderProductByOrderId(order.id)

        return OrderKitchenCreationPublishEvent(
            txId = txId,
            orderId = order.id,
            orderProducts = orderProducts,
        )
    }

    override fun approvalOrderKitchenEvent(txId: String): OrderKitchenStatusUpdatePublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenStatusUpdatePublishEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.APPROVAL,
        )
    }

    override fun rejectOrderKitchenEvent(txId: String): OrderKitchenStatusUpdatePublishEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenStatusUpdatePublishEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.REJECT,
        )
    }
}