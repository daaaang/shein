package com.order.domain.usecase

import com.order.domain.events.OrderKitchenCreationEvent
import com.order.domain.events.OrderKitchenStatusConsumeEvent

interface OrderKitchenUseCase {

    fun createOrderKitchenEvent(txId: String): OrderKitchenCreationEvent

    fun approvalOrderKitchenEvent(txId: String): OrderKitchenStatusConsumeEvent

    fun rejectOrderKitchenEvent(txId: String): OrderKitchenStatusConsumeEvent
}