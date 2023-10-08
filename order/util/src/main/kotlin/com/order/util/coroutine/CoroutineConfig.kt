package com.order.util.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {

    @Bean
    fun applyCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Default)
    }
}