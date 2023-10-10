package com.order.domain.events.handler

import com.order.domain.events.EventMessage
import com.order.domain.events.publisher.EventPublishName
import org.springframework.stereotype.Component

@Component
interface EventPublishHandler<T> {
    suspend fun handle(eventPublishName: EventPublishName, message: EventMessage<T>)
}