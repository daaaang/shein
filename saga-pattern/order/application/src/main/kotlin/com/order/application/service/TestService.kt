package com.order.application.service

import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventTarget
import com.order.domain.share.Logger
import org.springframework.stereotype.Service

@Service
class TestService(
    private val eventPublisher: EventPublisher,
) {

    fun runEvent() {
        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_TEST,
            message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = "1234",
                message = OrderUserPublishEvent(
                    txId = "1234",
                    userId = 150L,
                )
            )
        )
    }

    companion object : Logger()
}