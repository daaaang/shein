package com.order.domain.events.publisher

enum class EventPublishName(
    val topicName: String,
) {
    ORDER_TO_TEST("order-to-test"),
    ORDER_TO_USER("order-to-user-status"),
    ORDER_TO_KITCHEN_CREATION("order-to-kitchen-ticket-creation"),
    ORDER_TO_KITCHEN_STATUS("order-to-kitchen-ticket-status"),
    ORDER_TO_PAYMENT("order-to-payment-pay"),
    ORDER_TO_PAYMENT_STATUS("order-to-payment-pay-status"),
}