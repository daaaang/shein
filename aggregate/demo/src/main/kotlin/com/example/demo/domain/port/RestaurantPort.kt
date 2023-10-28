package com.example.demo.domain.port

import com.example.demo.domain.Restaurant
import org.springframework.stereotype.Component

@Component
interface RestaurantPort {
    fun save(restaurant: Restaurant)

    fun getByRestaurantId(id: Long): Restaurant
}