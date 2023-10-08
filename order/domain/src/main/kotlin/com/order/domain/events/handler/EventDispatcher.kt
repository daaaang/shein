package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.OrderKitchenEvent
import com.order.domain.events.OrderProductEvent
import org.springframework.stereotype.Component

@Component
class EventDispatcher(
    private val orderProductEventHandler: OrderProductEventHandler,
    private val kitchenEventHandler: OrderKitchenEventHandler,
) {
    suspend fun dispatch(event: Event) {
        when(event) {
            is OrderProductEvent -> orderProductEventHandler.handle(event)
            is OrderKitchenEvent -> kitchenEventHandler.handle(event)
        }
    }
}