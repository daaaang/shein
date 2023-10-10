package com.order.adapter.subscribe

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.consumer.OrderEventConsumer
import com.order.domain.events.dispatcher.EventConsumeDispatcher
import com.order.domain.share.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaOrderEventConsumer(
    private val eventConsumeDispatcher: EventConsumeDispatcher,
    private val coroutineScope: CoroutineScope,
) : OrderEventConsumer {

    @KafkaListener(topics = ["order-to-test"], groupId = "saga")
    override fun consumeTest(message: EventMessage<String>) {
        log.info("message = $message")
    }

    @KafkaListener(topics = ["user-to-order-status"], groupId = "saga")
    override fun consumeUserStatus(message: EventMessage<UserStatusConsumeEvent>) {
        val clazz = UserStatusConsumeEvent::class
        coroutineScope.launch {
            eventConsumeDispatcher.dispatch(message, clazz)
        }
    }

    @KafkaListener(topics = ["kitchen-to-order-ticket-creation"], groupId = "saga")
    override fun consumeKitchenTicketCreation(message: EventMessage<OrderKitchenTicketCreationConsumeEvent>) {

        val clazz = OrderKitchenTicketCreationConsumeEvent::class
        coroutineScope.launch {
            eventConsumeDispatcher.dispatch(message, clazz)
        }
    }

    @KafkaListener(topics = ["payment-to-order-pay"], groupId = "saga")
    override fun consumePayment(message: EventMessage<OrderPaymentStatusConsumeEvent>) {

        val clazz = OrderPaymentStatusConsumeEvent::class
        coroutineScope.launch {
            eventConsumeDispatcher.dispatch(message, clazz)
        }
    }

    @KafkaListener(topics = ["kitchen-to-order-ticket-status"], groupId = "saga")
    override fun consumeKitchenTicketApproval(message: EventMessage<OrderKitchenStatusConsumeEvent>) {

        val clazz = OrderKitchenStatusConsumeEvent::class
        coroutineScope.launch {
            eventConsumeDispatcher.dispatch(message, clazz)
        }
    }


    companion object : Logger()
}