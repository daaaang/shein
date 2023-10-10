package com.order.application.service

import com.order.application.port.OrderProductQueryPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderKitchenCreationEvent
import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import org.springframework.stereotype.Service

@Service
class OrderKitchenService(
    private val orderQueryPort: OrderQueryPort,
    private val orderProductQueryPort: OrderProductQueryPort,
) : OrderKitchenUseCase {

    override fun createOrderKitchenEvent(txId: String): OrderKitchenCreationEvent {
        val order = orderQueryPort.getOrderByTxId(txId)
        val orderProducts = orderProductQueryPort.getOrderProductByOrderId(order.id)

        return OrderKitchenCreationEvent(
            txId = txId,
            orderId = order.id,
            orderProducts = orderProducts,
        )
    }

    override fun approvalOrderKitchenEvent(txId: String): OrderKitchenStatusConsumeEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenStatusConsumeEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.APPROVAL,
        )
    }

    override fun rejectOrderKitchenEvent(txId: String): OrderKitchenStatusConsumeEvent {
        val order = orderQueryPort.getOrderByTxId(txId)

        return OrderKitchenStatusConsumeEvent(
            txId = txId,
            orderId = order.id,
            kitchenStatus = KitchenTicketStatusType.REJECT,
        )
    }
}