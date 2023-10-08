package com.order.adapter.publish

import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.share.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher<T>(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : EventPublisher<T> {
    override fun publish(eventName: EventPublishName, message: T) {

        log.info("message topic = ${eventName.topicName}, message = ${message.toString()}")
        kafkaTemplate.send(
            eventName.topicName,
            message.toString(),
        )
    }

    companion object : Logger()

}