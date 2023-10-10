package com.order.domain.events.publisher

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import org.springframework.stereotype.Component

@Component
interface EventPublisher {
    fun publish(
        eventName: EventPublishName,
        message: EventMessage<Event>,
    )
}