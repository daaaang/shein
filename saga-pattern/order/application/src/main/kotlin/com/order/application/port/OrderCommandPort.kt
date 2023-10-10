package com.order.application.port

import com.order.domain.model.Order
import org.springframework.stereotype.Component

@Component
interface OrderCommandPort {

    fun saveOrder(order: Order): Order
}