package com.order.domain.events.handler

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderPaymentEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.events.publisher.EventTarget
import org.springframework.stereotype.Component

@Component
class OrderPaymentEventHandler(
    private val eventPublisher: EventPublisher<EventMessage<OrderPaymentEvent>>
) : EventHandler<OrderPaymentEvent> {
    override suspend fun handle(event: OrderPaymentEvent) {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_PAYMENT,
            message = EventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = event.txId,
                status = EventStatus.APPROVED,
                message = event,
            ),
        )
    }
}