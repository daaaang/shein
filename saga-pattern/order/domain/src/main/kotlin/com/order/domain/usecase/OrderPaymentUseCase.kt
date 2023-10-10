package com.order.domain.usecase

import com.order.domain.events.OrderPaymentCreationPublishEvent
import com.order.domain.events.OrderPaymentStatusPublishEvent
import com.order.domain.model.ProductPrice
import org.springframework.stereotype.Component

@Component
interface OrderPaymentUseCase {

    fun createPaymentCreditEvent(txId: String, orderId: Long, productPrices: List<ProductPrice>): OrderPaymentCreationPublishEvent

    fun rejectPaymentCreditEvent(txId: String): OrderPaymentStatusPublishEvent
}