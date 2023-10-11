package com.order.domain.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.stereotype.Component

@Component
interface EventMixIn {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
    @JsonSubTypes(
        JsonSubTypes.Type(value = OrderUserPublishEvent::class, name = "OrderUserPublishEvent"),
        JsonSubTypes.Type(value = OrderPaymentCreationPublishEvent::class, name = "OrderPaymentCreationPublishEvent"),
        JsonSubTypes.Type(value = OrderKitchenCreationPublishEvent::class, name = "OrderKitchenCreationPublishEvent"),
        JsonSubTypes.Type(value = OrderPaymentStatusPublishEvent::class, name = "OrderPaymentStatusPublishEvent"),
        JsonSubTypes.Type(value = OrderKitchenTicketStatusUpdatePublishEvent::class, name = "OrderKitchenTicketStatusUpdatePublishEvent"),
        JsonSubTypes.Type(value = OrderKitchenTicketCreationConsumeEvent::class, name = "OrderKitchenTicketCreationConsumeEvent"),
        JsonSubTypes.Type(value = OrderPaymentStatusConsumeEvent::class, name = "OrderPaymentStatusConsumeEvent"),
        JsonSubTypes.Type(value = OrderKitchenTicketStatusConsumeEvent::class, name = "OrderKitchenTicketStatusConsumeEvent"),
        JsonSubTypes.Type(value = UserStatusConsumeEvent::class, name = "UserStatusConsumeEvent"),
    )
    fun getType(): String
}