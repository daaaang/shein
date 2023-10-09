package com.order.domain.events.publisher

enum class EventStatus(
    private val status: String,
) {
    APPROVED("approved"),
    REJECTED("rejected"),
}