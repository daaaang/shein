package com.order.application.port

import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
interface OrderProductCommandPort {

    fun saveOrderProduct(orderProduct: OrderProduct): OrderProduct

    fun saveOrderProducts(orderProducts: List<OrderProduct>): List<OrderProduct>
}