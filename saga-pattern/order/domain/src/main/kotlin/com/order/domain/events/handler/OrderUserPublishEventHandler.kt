package com.order.domain.events.handler

import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component

@Component
class OrderUserPublishEventHandler(
    private val eventPublisher: EventPublisher,
) : EventPublishHandler<OrderUserPublishEvent> {

    override suspend fun process(event: OrderUserPublishEvent) {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_USER,
            message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = event.txId,
                message = event,
            ),
        )
    }
}