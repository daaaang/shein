package com.order.domain.events

import com.order.domain.events.publisher.EventTarget

sealed class EventMessage<out T>(
    open val target: EventTarget,
    open val txId: String,
)

data class TargetEventMessage<T>(
    override val target: EventTarget,
    override val txId: String,
    val message: T,
) : EventMessage<T>(target = target, txId = txId)

data class ErrorEventMessage<T>(
    override val target: EventTarget,
    override val txId: String,
    val errorMessage: String,
) : EventMessage<T>(target = target, txId = txId)
