package com.order.application.service

import com.order.application.port.OrderCommandPort
import com.order.application.port.OrderProductCommandPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderUserEvent
import com.order.domain.model.Order
import com.order.domain.model.OrderRequest
import com.order.domain.model.StepStatus
import com.order.domain.usecase.OrderUseCase
import kotlinx.coroutines.CoroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderQueryPort: OrderQueryPort,
    private val orderCommandPort: OrderCommandPort,
    private val orderProductCommandPort: OrderProductCommandPort,
    private val coroutineScope: CoroutineScope,
): OrderUseCase {

    @Transactional
    override fun createOrder(orderRequest: OrderRequest) {

        val savedOrder = orderCommandPort.saveOrder(
            order = Order.fromOrderRequest(orderRequest),
        )

        orderProductCommandPort.saveOrderProducts(
            orderProducts = orderRequest.orderProducts
        )

        val orderEvent = OrderUserEvent(
            txId = savedOrder.txId,
            userId = savedOrder.userId,
        )
    }

    override fun approvalOrder(txId: String) {
        val order = orderQueryPort.getOrderByTxId(txId)
        val updatedOrder = order.updateOrderStatus(StepStatus.APPROVED)
        orderCommandPort.saveOrder(updatedOrder)
    }

    override fun rejectOrder(txId: String, orderRejectReason: String) {
        val order = orderQueryPort.getOrderByTxId(txId)
        val updatedOrder = order.updateOrderStatus(StepStatus.REJECTED)
        orderCommandPort.saveOrder(updatedOrder)
        TODO("주문 실패 메세지")
    }
}