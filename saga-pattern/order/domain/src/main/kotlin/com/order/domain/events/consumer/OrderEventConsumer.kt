package com.order.domain.events.consumer

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderKitchenTicketStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.events.UserStatusConsumeEvent
import org.springframework.stereotype.Component

@Component
interface OrderEventConsumer {

    fun consumeTest(message: EventMessage<OrderUserPublishEvent>)

    fun consumeUserStatus(message: EventMessage<UserStatusConsumeEvent>)

    fun consumeKitchenTicketCreation(message: EventMessage<OrderKitchenTicketCreationConsumeEvent>)

    fun consumePayment(message: EventMessage<OrderPaymentStatusConsumeEvent>)

    fun consumeKitchenTicketStatus(message: EventMessage<OrderKitchenTicketStatusConsumeEvent>)
}