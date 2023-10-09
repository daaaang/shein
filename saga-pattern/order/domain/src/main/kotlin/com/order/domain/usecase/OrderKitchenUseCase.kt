package com.order.domain.usecase

import com.order.domain.events.OrderKitchenEvent

interface OrderKitchenUseCase {
    fun createOrderKitchenEvent(txId: String): OrderKitchenEvent
}