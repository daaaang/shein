package com.order.adapter.publish

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import com.order.domain.events.publisher.EventPublisher
import com.order.domain.events.publisher.EventPublishName
import com.order.domain.share.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : EventPublisher {
    override fun publish(eventName: EventPublishName, message: EventMessage<Event>) {

        log.info("message topic = ${eventName.topicName}, message = $message")
        kafkaTemplate.send(
            eventName.topicName,
            message.toString(),
        )
    }

    companion object : Logger()

}