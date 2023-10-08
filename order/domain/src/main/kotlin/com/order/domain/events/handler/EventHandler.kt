package com.order.domain.events.handler

import org.springframework.stereotype.Component

@Component
interface EventHandler<T> {
    suspend fun handle(event: T)
}