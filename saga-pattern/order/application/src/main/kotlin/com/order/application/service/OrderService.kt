package com.order.application.service

import com.order.application.port.OrderCommandPort
import com.order.application.port.OrderProductCommandPort
import com.order.domain.events.OrderUserEvent
import com.order.domain.events.handler.EventHandler
import com.order.domain.model.Order
import com.order.domain.model.OrderRejectReason
import com.order.domain.model.OrderRequest
import com.order.domain.usecase.OrderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val eventHandler: EventHandler<OrderUserEvent>,
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

        coroutineScope.launch {
            eventHandler.handle(orderEvent)
        }
    }

    override fun rejectOrder(txId: String, orderRejectReason: OrderRejectReason) {
        TODO("Not yet implemented")
    }
}