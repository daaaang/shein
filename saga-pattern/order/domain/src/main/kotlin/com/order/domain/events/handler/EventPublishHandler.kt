package com.order.domain.events.handler

import org.springframework.stereotype.Component

@Component
interface EventPublishHandler<T> {
    suspend fun process(event: T)
}