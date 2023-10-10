package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.EventMessageCreator
import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.usecase.OrderPaymentUseCase
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Component

@Component
class OrderStatusEventHandler(
    private val orderUseCase: OrderUseCase,
    private val eventPublisher: EventPublisher,
    private val orderPaymentUseCase: OrderPaymentUseCase,
) : EventHandler<OrderKitchenStatusConsumeEvent> {
    override suspend fun process(event: OrderKitchenStatusConsumeEvent) {

        when (event.kitchenStatus) {
            KitchenTicketStatusType.APPROVAL -> orderUseCase.approvalOrder(txId = event.txId)

            KitchenTicketStatusType.REJECT -> {
                reject(txId = event.txId)
            }
        }
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        val rejectOrderPaymentEvent = rejectOrderPayment(txId = txId)

        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_PAYMENT,
            message = rejectOrderPaymentEvent,
        )
    }

    private suspend fun rejectOrderPayment(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderPaymentUseCase.rejectPaymentCreditEvent(txId)
            }
        )
    }

}