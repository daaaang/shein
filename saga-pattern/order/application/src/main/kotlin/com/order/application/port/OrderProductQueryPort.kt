package com.order.application.port

import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
interface OrderProductQueryPort {

    fun getOrderProductByOrderId(orderId: Long): List<OrderProduct>
}