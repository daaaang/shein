package com.order.domain.usecase

import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.OrderKitchenTicketStatusUpdatePublishEvent

interface OrderKitchenUseCase {

    fun createOrderKitchenEvent(txId: String): OrderKitchenCreationPublishEvent

    fun approvalOrderKitchenEvent(txId: String): OrderKitchenTicketStatusUpdatePublishEvent

    fun rejectOrderKitchenEvent(txId: String): OrderKitchenTicketStatusUpdatePublishEvent
}