package com.order.domain.usecase

import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.model.Order
import org.springframework.stereotype.Component

@Component
interface OrderUserUseCase {

    fun createOrderUserPublishEvent(order: Order): OrderUserPublishEvent

}