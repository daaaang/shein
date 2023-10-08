package com.order.domain.events.handler

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderProductEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component

@Component
class OrderProductEventHandler(
    private val eventPublisher: EventPublisher<EventMessage<OrderProductEvent>>,
) : EventHandler<OrderProductEvent> {
    override suspend fun handle(event: OrderProductEvent) {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_PRODUCT_STOCK,
            message = EventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = event.txId,
                status = EventStatus.APPROVED,
                message = event,
            )
        )
    }
}