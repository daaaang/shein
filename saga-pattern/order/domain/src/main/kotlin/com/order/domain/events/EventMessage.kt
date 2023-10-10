package com.order.domain.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.order.domain.events.publisher.EventTarget


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = TargetEventMessage::class, name = "TargetEventMessage"),
    JsonSubTypes.Type(value = ErrorEventMessage::class, name = "ErrorEventMessage"),
)
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
