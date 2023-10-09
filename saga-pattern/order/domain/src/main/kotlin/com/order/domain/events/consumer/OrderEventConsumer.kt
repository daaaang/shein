package com.order.domain.events.consumer

import com.order.domain.events.EventMessage
import com.order.domain.model.KitchenOrder
import com.order.domain.model.PaymentStatus
import com.order.domain.model.UserStatus
import org.springframework.stereotype.Component

@Component
interface OrderEventConsumer {

    fun consumeTest(message: EventMessage<String>)

    fun consumeUserStatus(message: EventMessage<UserStatus>)

    fun consumeKitchenTicket(message: EventMessage<KitchenOrder>)

    fun consumePayment(message: EventMessage<PaymentStatus>)
}