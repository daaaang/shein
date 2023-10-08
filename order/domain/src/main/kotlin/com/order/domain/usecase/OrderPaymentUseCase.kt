package com.order.domain.usecase

import com.order.domain.events.OrderPaymentEvent
import org.springframework.stereotype.Component

@Component
interface OrderPaymentUseCase {

    fun createPaymentCreditEvent(txId: String): OrderPaymentEvent
}