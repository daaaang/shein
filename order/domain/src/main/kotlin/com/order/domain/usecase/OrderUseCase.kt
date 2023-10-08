package com.order.domain.usecase

import com.order.domain.model.Order
import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
interface OrderUseCase {

    fun saveOrder(order: Order): Order

    fun saveOrderProduct(orderProducts: OrderProduct): OrderProduct

    fun rejectOrder(order: Order)
}