package com.order.adapter.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.order.domain.events.Event
import com.order.domain.events.EventMixIn
import com.order.domain.events.OrderConsumeEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class ObjectConfig {

    @Bean
    @Primary
    @Qualifier("defaultObjectMapper")
    fun applyDefaultObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    @Qualifier("eventObjectMapper")
    fun applyEventObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.addMixIn(Event::class.java, EventMixIn::class.java)
        objectMapper.addMixIn(OrderConsumeEvent::class.java, EventMixIn::class.java)
        return objectMapper
    }
}