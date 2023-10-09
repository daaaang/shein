package com.order.domain.events.handler

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderUserEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component

@Component
class OrderUserEventHandler(
    private val eventPublisher: EventPublisher<EventMessage<OrderUserEvent>>,
) : EventHandler<OrderUserEvent> {
    override suspend fun handle(event: OrderUserEvent) {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_USER,
            message = EventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = event.txId,
                status = EventStatus.APPROVED,
                message = event,
            )
        )
    }
}