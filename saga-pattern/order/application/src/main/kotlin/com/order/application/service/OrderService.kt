package com.order.application.service

import com.order.application.port.OrderCommandPort
import com.order.application.port.OrderProductCommandPort
import com.order.application.port.OrderQueryPort
import com.order.domain.model.Order
import com.order.domain.model.OrderProduct
import com.order.domain.model.OrderRequest
import com.order.domain.model.StepStatus
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderQueryPort: OrderQueryPort,
    private val orderCommandPort: OrderCommandPort,
    private val orderProductCommandPort: OrderProductCommandPort,
) : OrderUseCase {

    @Transactional
    override fun createOrder(orderRequest: OrderRequest): Order {

        val savedOrder = orderCommandPort.saveOrder(
            order = Order.fromOrderRequest(orderRequest),
        )

        orderProductCommandPort.saveOrderProducts(
            orderProducts = orderRequest.productItems
                .map {
                    OrderProduct(
                        orderId = savedOrder.id,
                        productId = it.productId,
                        amount = it.amount,
                    )
                }
        )

        return savedOrder
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
    }
}