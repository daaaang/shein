package com.order.application.port

import com.order.domain.model.Order
import org.springframework.stereotype.Component

@Component
interface OrderQueryPort {
    fun getOrderByTxId(txId: String): Order

    fun getOrderByOrderId(orderId: Long): Order
}