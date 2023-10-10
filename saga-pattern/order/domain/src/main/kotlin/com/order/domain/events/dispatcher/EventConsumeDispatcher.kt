package com.order.domain.events.dispatcher

import com.order.domain.events.ErrorEventMessage
import com.order.domain.events.EventMessage
import com.order.domain.events.OrderConsumeEvent
import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.handler.OrderKitchenTicketCreationHandler
import com.order.domain.events.handler.OrderKitchenTicketStatusHandler
import com.order.domain.events.handler.OrderPaymentCreationEventHandler
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventConsumeDispatcher(
    private val orderKitchenTicketCreationHandler: OrderKitchenTicketCreationHandler,
    private val orderKitchenTicketStatusHandler: OrderKitchenTicketStatusHandler,
    private val orderPaymentCreationEventHandler: OrderPaymentCreationEventHandler,
    private val orderUseCase: OrderUseCase,
) {
    suspend fun dispatch(message: EventMessage<OrderConsumeEvent>, clazz: KClass<out OrderConsumeEvent>) {
        when (message) {
            is TargetEventMessage<OrderConsumeEvent> -> {
                when(val event = message.message) {
                    is UserStatusConsumeEvent -> orderKitchenTicketCreationHandler.process(event)
                    is OrderKitchenTicketCreationConsumeEvent -> orderPaymentCreationEventHandler.process(event)
                    is OrderPaymentStatusConsumeEvent -> orderKitchenTicketStatusHandler.process(event)
                    is OrderKitchenStatusConsumeEvent -> orderUseCase.approvalOrder(event.txId)
                }
            }

            is ErrorEventMessage<OrderConsumeEvent> -> {
                when(clazz) {
                    UserStatusConsumeEvent::class -> TODO("")
                    OrderKitchenTicketCreationConsumeEvent::class -> TODO("")
                    OrderPaymentStatusConsumeEvent::class -> TODO("")
                    OrderKitchenStatusConsumeEvent::class -> TODO("")
                }
            }
        }
    }
}