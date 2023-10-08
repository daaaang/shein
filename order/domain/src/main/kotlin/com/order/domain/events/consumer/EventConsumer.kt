package com.order.domain.events.consumer

import com.order.domain.events.EventMessage
import com.order.domain.model.ProductOrderBill
import org.springframework.stereotype.Component

@Component
interface EventConsumer {

    fun consumeTest(message: EventMessage<String>)

    fun consumeProductStock(message: EventMessage<ProductOrderBill>)

}