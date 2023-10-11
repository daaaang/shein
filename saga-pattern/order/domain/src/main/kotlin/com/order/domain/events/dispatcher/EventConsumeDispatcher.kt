package com.order.domain.events.dispatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.order.domain.events.ErrorEventMessage
import com.order.domain.events.EventMessage
import com.order.domain.events.OrderConsumeEvent
import com.order.domain.events.OrderKitchenTicketStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.handler.OrderKitchenTicketCreationHandler
import com.order.domain.events.handler.OrderKitchenTicketStatusHandler
import com.order.domain.events.handler.OrderPaymentCreationEventHandler
import com.order.domain.events.handler.OrderStatusEventHandler
import com.order.domain.share.Logger
import com.order.domain.usecase.OrderUseCase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventConsumeDispatcher(
    private val orderKitchenTicketCreationHandler: OrderKitchenTicketCreationHandler,
    private val orderKitchenTicketStatusHandler: OrderKitchenTicketStatusHandler,
    private val orderPaymentCreationEventHandler: OrderPaymentCreationEventHandler,
    private val orderStatusEventHandler: OrderStatusEventHandler,
    private val orderUseCase: OrderUseCase,
    @Qualifier("eventObjectMapper") private val objectMapper: ObjectMapper,
) {
    suspend fun dispatch(message: EventMessage<OrderConsumeEvent>, clazz: KClass<out OrderConsumeEvent>) {
        when (message) {
            is TargetEventMessage<OrderConsumeEvent> -> {
                val event = objectMapper.convertValue(message.message, OrderConsumeEvent::class.java)
                log.info("event = $event")

                when(event) {
                    is UserStatusConsumeEvent -> orderKitchenTicketCreationHandler.process(event)
                    is OrderKitchenTicketCreationConsumeEvent -> orderPaymentCreationEventHandler.process(event)
                    is OrderPaymentStatusConsumeEvent -> orderKitchenTicketStatusHandler.process(event)
                    is OrderKitchenTicketStatusConsumeEvent -> orderStatusEventHandler.process(event)
                }
            }

            is ErrorEventMessage<OrderConsumeEvent> -> {
                when(clazz) {
                    UserStatusConsumeEvent::class -> orderUseCase.rejectOrder(txId = message.txId, orderRejectReason = message.errorMessage)
                    OrderKitchenTicketCreationConsumeEvent::class -> orderUseCase.rejectOrder(txId = message.txId, orderRejectReason = message.errorMessage)
                    OrderPaymentStatusConsumeEvent::class -> orderKitchenTicketCreationHandler.reject(message.txId, rejectReason = message.errorMessage)
                    OrderKitchenTicketStatusConsumeEvent::class -> orderPaymentCreationEventHandler.reject(message.txId, rejectReason = message.errorMessage)
                }
            }
        }
    }

    companion object : Logger()
}