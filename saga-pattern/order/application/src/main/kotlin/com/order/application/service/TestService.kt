package com.order.application.service

import com.order.domain.events.publisher.EventPublisher
import com.order.domain.share.Logger
import org.springframework.stereotype.Service

@Service
class TestService(
    private val eventPublisher: EventPublisher<String>,
) {

    fun runEvent() {
    }

    companion object : Logger()
}