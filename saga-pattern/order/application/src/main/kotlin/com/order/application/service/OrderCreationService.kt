package com.order.application.service

import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.events.handler.EventPublishHandler
import com.order.domain.model.OrderRequest
import com.order.domain.model.ProductItem
import com.order.domain.usecase.OrderUseCase
import com.order.domain.usecase.OrderUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderCreationService(
    private val orderUseCase: OrderUseCase,
    private val orderUserUseCase: OrderUserUseCase,
    private val coroutineScope: CoroutineScope,
    private val orderUserPublishEventHandler: EventPublishHandler<OrderUserPublishEvent>,
) {

    @Transactional
    fun createOrder(orderRequest: OrderRequest) {
        validateOrderRequest(orderRequest.productItems)

        val order = orderUseCase.createOrder(orderRequest)

        val orderUserEvent = orderUserUseCase.createOrderUserPublishEvent(order)

        coroutineScope.launch {
            orderUserPublishEventHandler.process(orderUserEvent)
        }
    }

    private fun validateOrderRequest(productItems: List<ProductItem>) {
        val productItemMap = productItems.groupBy { it.productId }
        require(productItemMap.keys.size == productItems.size) { throw IllegalArgumentException() }
    }

}