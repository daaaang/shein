package com.example.demo.domain

import java.time.LocalDateTime

data class Restaurant(
    val name: String,
    val orderId: Long,
    val address: Address,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
    val id: Long = 0L,
) {

    companion object {
        fun of(
            name: String,
            orderId: Long,
            address: Address,
            createdAt: LocalDateTime = LocalDateTime.now(),
            modifiedAt: LocalDateTime = LocalDateTime.now(),
        ): Restaurant {
            return Restaurant(
                name = name,
                orderId = orderId,
                address = address,
                createdAt = createdAt,
                modifiedAt = modifiedAt,
            )
        }
    }

}