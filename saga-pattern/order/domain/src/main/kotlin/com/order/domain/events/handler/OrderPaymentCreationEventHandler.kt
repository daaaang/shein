package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.EventMessageCreator
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.usecase.OrderPaymentUseCase
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Component

@Component
class OrderPaymentCreationEventHandler(
    private val orderUseCase: OrderUseCase,
    private val orderPaymentUseCase: OrderPaymentUseCase,
    private val eventPublisher: EventPublisher,
) : EventHandler<OrderKitchenTicketCreationConsumeEvent> {

    override suspend fun process(event: OrderKitchenTicketCreationConsumeEvent) {
        val paymentEventMessage = createPaymentEventMessage(event)

        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_PAYMENT,
            message = paymentEventMessage,
        )
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        orderUseCase.rejectOrder(
            txId = txId,
            orderRejectReason = rejectReason
        )
    }

    private suspend fun createPaymentEventMessage(event: OrderKitchenTicketCreationConsumeEvent): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = event.txId,
            eventAction = {
                orderPaymentUseCase.createPaymentCreditEvent(
                    txId = event.txId,
                    orderId = event.orderId,
                    productPrices = event.productPrices,
                )
            }
        )
    }
}