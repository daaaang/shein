package com.example.demo.adapter

import com.example.demo.adapter.entity.RestaurantEntity
import com.example.demo.adapter.persistance.RestaurantEntityRepository
import com.example.demo.domain.Restaurant
import com.example.demo.domain.port.RestaurantPort
import org.springframework.stereotype.Component

@Component
class RestaurantAdapter(
    private val restaurantEntityRepository: RestaurantEntityRepository,
) : RestaurantPort {

    override fun save(restaurant: Restaurant) {
        restaurantEntityRepository.save(RestaurantEntity.from(restaurant))
    }

    override fun getByRestaurantId(id: Long): Restaurant {
        return restaurantEntityRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException() }
            .toDomainModel()
    }
}