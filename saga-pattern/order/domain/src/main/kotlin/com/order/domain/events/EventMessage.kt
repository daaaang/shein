package com.order.domain.events

import com.order.domain.events.publisher.EventTarget

sealed class EventMessage<T>(
    open val target: EventTarget,
    open val txId: String,
) {
    data class SuccessEventMessage<T>(
        override val target: EventTarget,
        override val txId: String,
        val message: T,
    ) : EventMessage<T>(target = target, txId = txId)

    data class FailEventMessage<T>(
        override val target: EventTarget,
        override val txId: String,
        val errorMessage: String,
    ) : EventMessage<T>(target = target, txId = txId)
}
