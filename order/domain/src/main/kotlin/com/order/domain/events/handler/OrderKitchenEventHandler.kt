package com.order.domain.events.handler

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderKitchenEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component

@Component
class OrderKitchenEventHandler(
    private val eventPublisher: EventPublisher<EventMessage<OrderKitchenEvent>>,
) : EventHandler<OrderKitchenEvent> {

    override suspend fun handle(event: OrderKitchenEvent) {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_KITCHEN_STATUS,
            message = EventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = event.txId,
                status = EventStatus.APPROVED,
                message = event,
            )
        )
    }
}