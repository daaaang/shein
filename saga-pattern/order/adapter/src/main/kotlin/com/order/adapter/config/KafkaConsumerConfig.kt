package com.order.adapter.config

import com.order.domain.events.Event
import com.order.domain.events.EventMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, EventMessage<Event>> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "saga"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java

        val jsonDeserializer = JsonDeserializer<EventMessage<Event>>(EventMessage::class.java).apply {
            setUseTypeHeaders(true)
        }

        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), jsonDeserializer)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, EventMessage<Event>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, EventMessage<Event>>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

}