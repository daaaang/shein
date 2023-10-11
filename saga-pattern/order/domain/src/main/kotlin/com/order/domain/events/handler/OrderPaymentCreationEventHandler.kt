package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.EventMessageCreator
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderPaymentUseCase
import org.springframework.stereotype.Component

@Component
class OrderPaymentCreationEventHandler(
    private val orderKitchenUseCase: OrderKitchenUseCase,
    private val orderPaymentUseCase: OrderPaymentUseCase,
    private val eventPublisher: EventPublisher,
) : EventHandler<OrderKitchenTicketCreationConsumeEvent> {

    override suspend fun process(event: OrderKitchenTicketCreationConsumeEvent) {
        when (event.kitchenStatus) {
            KitchenTicketCreationType.PENDING -> {
                val paymentEventMessage = createPaymentEventMessage(event)

                eventPublisher.publish(
                    eventName = EventPublishName.ORDER_TO_PAYMENT,
                    message = paymentEventMessage,
                )
            }
            KitchenTicketCreationType.REJECTED_FULL_WAITING -> {
                reject(txId = event.txId)
            }
        }
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        val kitchenTicketRejectEvent = updateRejectKitchenTicketCreationEventMessage(txId)
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_KITCHEN_CREATION,
            message = kitchenTicketRejectEvent,
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

    private suspend fun updateRejectKitchenTicketCreationEventMessage(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderKitchenUseCase.rejectOrderKitchenEvent(txId = txId)
            }
        )
    }
}