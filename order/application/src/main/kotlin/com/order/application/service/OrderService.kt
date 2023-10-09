package com.order.application.service

import com.order.domain.usecase.OrderUseCase
import com.order.domain.events.OrderUserEvent
import com.order.domain.events.handler.EventHandler
import com.order.domain.model.Order
import com.order.domain.model.OrderItems
import com.order.domain.model.OrderRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val eventHandler: EventHandler<OrderUserEvent>,
    private val orderUseCase: OrderUseCase,
    private val coroutineScope: CoroutineScope,
) {

    @Transactional
    fun createOrder(orderRequest: OrderRequest) {
        val savedOrder = orderUseCase.saveOrder(
            order = Order.fromOrderRequest(orderRequest),
        )

        val saveOrderItems = orderUseCase.saveOrderProduct(
            orderProducts = OrderItems(
                orderId = savedOrder.id,
                orderProducts = orderRequest.orderProducts,
            )
        )

        val orderEvent = OrderUserEvent(
            txId = savedOrder.txId,
            userId = savedOrder.userId,
        )

        coroutineScope.launch {
            eventHandler.handle(orderEvent)
        }
    }
}