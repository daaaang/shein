package com.order.domain.model.payment

data class PaymentStatus(
    val userId: Long,
    val orderId: Long,
)