package com.order.domain.events.publisher

enum class EventTarget(
    private val target: String,
) {
    ORDER_CREATION("order-creation"),
}