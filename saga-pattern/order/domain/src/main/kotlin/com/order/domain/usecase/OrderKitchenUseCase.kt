package com.order.domain.usecase

import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.OrderKitchenStatusUpdatePublishEvent

interface OrderKitchenUseCase {

    fun createOrderKitchenEvent(txId: String): OrderKitchenCreationPublishEvent

    fun approvalOrderKitchenEvent(txId: String): OrderKitchenStatusUpdatePublishEvent

    fun rejectOrderKitchenEvent(txId: String): OrderKitchenStatusUpdatePublishEvent
}