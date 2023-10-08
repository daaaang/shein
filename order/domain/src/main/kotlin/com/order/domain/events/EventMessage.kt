package com.order.domain.events

import com.order.domain.events.publisher.EventStatus
import com.order.domain.events.publisher.EventTarget

data class EventMessage<T>(
    val target: EventTarget,
    val txId: String,
    val status: EventStatus,
    val message: T,
)