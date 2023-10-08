package com.order.application.service

import com.order.application.port.OrderProductQueryPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderKitchenEvent
import com.order.domain.usecase.OrderKitchenUseCase
import org.springframework.stereotype.Service

@Service
class OrderKitchenService(
    private val orderQueryPort: OrderQueryPort,
    private val orderProductQueryPort: OrderProductQueryPort,
) : OrderKitchenUseCase {
    override fun createOrderKitchenEvent(txId: String): OrderKitchenEvent {
        val order = orderQueryPort.getOrderByTxId(txId)
        val orderProducts = orderProductQueryPort.getOrderProductByOrderId(order.id)

        return OrderKitchenEvent(
            txId = txId,
            orderId = order.id,
            orderProducts = orderProducts,
        )
    }
}