package com.order.domain.usecase

import com.order.domain.model.OrderRequest
import org.springframework.stereotype.Component

@Component
interface OrderUseCase {

    fun createOrder(orderRequest: OrderRequest)

    fun approvalOrder(txId: String)

    fun rejectOrder(txId: String, orderRejectReason: String = "")
}