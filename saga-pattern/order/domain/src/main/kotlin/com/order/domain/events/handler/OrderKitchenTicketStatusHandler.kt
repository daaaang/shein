package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.EventMessageCreator
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import org.springframework.stereotype.Component

@Component
class OrderKitchenTicketStatusHandler(
    private val orderKitchenUseCase: OrderKitchenUseCase,
    private val eventPublisher: EventPublisher,
) : EventHandler<OrderPaymentStatusConsumeEvent> {
    override suspend fun process(event: OrderPaymentStatusConsumeEvent) {
        when(event.paymentStatus) {
            PaymentStatusType.APPROVAL -> {
                val paymentStatusEvent =  updateApprovalKitchenStatusEvent(event.txId)

                eventPublisher.publish(
                    eventName = EventPublishName.ORDER_TO_KITCHEN_STATUS,
                    message = paymentStatusEvent,
                )
            }
            PaymentStatusType.REJECT -> {
                TODO("보상 트랜잭션")
            }
        }
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        TODO("Not yet implemented")
    }


    private suspend fun updateApprovalKitchenStatusEvent(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderKitchenUseCase.approvalOrderKitchenEvent(txId)
            }
        )
    }
}