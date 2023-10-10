package com.order.application.service

import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.model.Order
import com.order.domain.usecase.OrderUserUseCase
import org.springframework.stereotype.Component

@Component
class OrderUserService : OrderUserUseCase {
    override fun createOrderUserPublishEvent(order: Order): OrderUserPublishEvent {
        return OrderUserPublishEvent(
            txId = order.txId,
            userId = order.userId,
        )
    }
}