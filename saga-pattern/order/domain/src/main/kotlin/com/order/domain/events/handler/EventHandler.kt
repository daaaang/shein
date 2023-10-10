package com.order.domain.events.handler

import org.springframework.stereotype.Component

@Component
interface EventHandler<T> {
    suspend fun process(event: T)

    suspend fun reject(txId: String, rejectReason: String = "")
}