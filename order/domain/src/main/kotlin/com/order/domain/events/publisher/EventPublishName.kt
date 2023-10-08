package com.order.domain.events.publisher

enum class EventPublishName(
    val topicName: String,
) {
    ORDER_TO_TEST("order-to-test"),
    ORDER_TO_PRODUCT_STOCK("order-to-product-stock"),
    ORDER_TO_CUSTOMER_PAYMENT("order-to-customer-payment"),
    ORDER_TO_KITCHEN_STATUS("order-to-kitchen-status"),
}