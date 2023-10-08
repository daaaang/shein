package com.order.application.service

import com.order.domain.usecase.OrderUseCase
import com.order.domain.events.OrderProductEvent
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
    private val eventHandler: EventHandler<OrderProductEvent>,
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

        val orderEvent = OrderProductEvent(
            txId = savedOrder.txId,
            orderId = savedOrder.id,
            orderProducts = saveOrderItems.orderProducts,
        )

        coroutineScope.launch {
            eventHandler.handle(orderEvent)
        }
    }
}