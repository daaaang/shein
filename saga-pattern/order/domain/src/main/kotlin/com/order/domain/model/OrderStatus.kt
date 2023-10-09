package com.order.domain.model

enum class OrderStatus(
    val status: String,
) {
    PENDING("주문"),
    APPROVED("승인"),
    REJECTED("거절"),
}