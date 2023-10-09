package com.order.domain.usecase

import com.order.domain.events.OrderPaymentEvent
import com.order.domain.model.ProductPrice
import org.springframework.stereotype.Component

@Component
interface OrderPaymentUseCase {
    fun createPaymentCreditEvent(
        txId: String,
        orderId: Long,
        productPrices: List<ProductPrice>,
    ): OrderPaymentEvent
}