package com.order.domain.usecase

import com.order.domain.model.OrderRejectReason
import com.order.domain.model.OrderRequest
import org.springframework.stereotype.Component

@Component
interface OrderUseCase {

    fun createOrder(orderRequest: OrderRequest)

    fun rejectOrder(txId: String, orderRejectReason: String)
}