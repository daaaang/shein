package com.order.domain.usecase

import com.order.domain.events.OrderPaymentCreationEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.model.ProductPrice
import org.springframework.stereotype.Component

@Component
interface OrderPaymentUseCase {

    fun createPaymentCreditEvent(txId: String, orderId: Long, productPrices: List<ProductPrice>): OrderPaymentCreationEvent

    fun rejectPaymentCreditEvent(txId: String): OrderPaymentStatusConsumeEvent
}