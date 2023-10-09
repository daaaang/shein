package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.OrderKitchenEvent
import com.order.domain.events.OrderPaymentEvent
import com.order.domain.events.OrderUserEvent
import org.springframework.stereotype.Component

@Component
class EventDispatcher(
    private val userEventHandler: OrderUserEventHandler,
    private val kitchenEventHandler: OrderKitchenEventHandler,
    private val paymentEventHandler: OrderPaymentEventHandler,
) {
    suspend fun dispatch(event: Event) {
        when(event) {
            is OrderUserEvent -> userEventHandler.handle(event)
            is OrderKitchenEvent -> kitchenEventHandler.handle(event)
            is OrderPaymentEvent -> paymentEventHandler.handle(event)
        }
    }
}