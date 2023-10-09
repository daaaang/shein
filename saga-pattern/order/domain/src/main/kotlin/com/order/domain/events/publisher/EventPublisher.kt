package com.order.domain.events.publisher

import org.springframework.stereotype.Component

@Component
interface EventPublisher<T> {
    fun publish(
        eventName: EventPublishName,
        message: T,
    )
}