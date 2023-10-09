package com.order.domain.events.publisher

enum class EventPublishName(
    val topicName: String,
) {
    ORDER_TO_TEST("order-to-test"),
    ORDER_TO_USER("order-to-user"),
    ORDER_TO_KITCHEN("order-to-kitchen"),
    ORDER_TO_PAYMENT("order-to-payment"),
}