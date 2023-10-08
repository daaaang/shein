package com.order.adapter.subscribe

import com.order.domain.events.EventMessage
import com.order.domain.events.OrderKitchenEvent
import com.order.domain.events.consumer.EventConsumer
import com.order.domain.events.handler.EventDispatcher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.share.Logger
import com.order.domain.model.ProductOrderBill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaEventConsumer(
    private val eventDispatcher: EventDispatcher,
    private val coroutineScope: CoroutineScope,
) : EventConsumer {

    @KafkaListener(topics = ["order-to-test"], groupId = "saga")
    override fun consumeTest(message: EventMessage<String>) {
        log.info("message = $message")
    }

    @KafkaListener(topics = ["product-to-order-product-stock"], groupId = "saga")
    override fun consumeProductStock(message: EventMessage<ProductOrderBill>) {

        coroutineScope.launch {
            when(message.status) {
                EventStatus.APPROVED -> eventDispatcher.dispatch(
                    event = OrderKitchenEvent.fromOrderProductBill(message.message)
                )
                EventStatus.REJECTED -> eventDispatcher.dispatch(
                    TODO("보상 트랜잭션")
                )
            }
        }
    }

    companion object: Logger()
}