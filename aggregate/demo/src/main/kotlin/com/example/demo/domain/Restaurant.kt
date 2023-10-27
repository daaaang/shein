package com.example.demo.domain

data class Restaurant(
    val address: Address,
    val name: Long,
    val orderId: Long,
    val id: Long = 0L,
)