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
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Component

@Component
class OrderKitchenTicketStatusHandler(
    private val orderKitchenUseCase: OrderKitchenUseCase,
    private val orderUseCase: OrderUseCase,
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
            PaymentStatusType.REJECT_DURING_PAYMENT -> {
                reject(txId = event.txId)
            }

            /* 주방 APPROVAL 실패로 결제 취소를 한 후 보상 트랜잭션*/
            PaymentStatusType.REJECT_AFTER_PAYMENT -> {
                orderUseCase.rejectOrder(txId = event.txId)
            }
        }
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        val orderKitchenStatusEvent = updateRejectKitchenStatusEvent(txId)

        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_KITCHEN_STATUS,
            message = orderKitchenStatusEvent,
        )
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

    private suspend fun updateRejectKitchenStatusEvent(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderKitchenUseCase.rejectOrderKitchenEvent(txId)
            }
        )
    }
}