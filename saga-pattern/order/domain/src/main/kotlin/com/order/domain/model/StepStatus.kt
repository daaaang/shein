package com.order.domain.model

enum class StepStatus(
    val status: String,
) {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절"),
}