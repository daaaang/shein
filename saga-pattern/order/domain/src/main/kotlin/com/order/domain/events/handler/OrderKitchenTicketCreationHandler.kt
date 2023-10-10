package com.order.domain.events.handler

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.EventMessageCreator
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.OrderRejectReason
import com.order.domain.model.user.UserStatusType
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderUseCase
import org.springframework.stereotype.Component

@Component
class OrderKitchenTicketCreationHandler(
    private val orderUseCase: OrderUseCase,
    private val orderKitchenUseCase: OrderKitchenUseCase,
    private val eventPublisher: EventPublisher,
) : EventHandler<UserStatusConsumeEvent> {
    override suspend fun process(event: UserStatusConsumeEvent) {

        when (event.userStatus) {
            UserStatusType.NOMAL -> {
                val orderKitchenTicketEventMessage= createOrderKitchenTicketEvent(txId = event.txId)
                eventPublisher.publish(
                    eventName = EventPublishName.ORDER_TO_KITCHEN_CREATION,
                    message = orderKitchenTicketEventMessage,
                )
            }

            UserStatusType.ABNOMAL -> {
                reject(txId = event.txId)
            }
        }
    }

    override suspend fun reject(txId: String, rejectReason: String) {
        orderUseCase.rejectOrder(
            txId = txId,
            orderRejectReason = OrderRejectReason.USER_ABNOMAL.name
        )
    }

    private suspend fun  createOrderKitchenTicketEvent(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderKitchenUseCase.createOrderKitchenEvent(txId = txId)
            }
        )
    }
}