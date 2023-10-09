package com.order.domain.model

data class PaymentStatus(
    val userId: Long,
    val orderId: Long,
    val paymentStatusType: PaymentStatusType,
)