package com.example.demo.adapter.entity

import com.example.demo.domain.Address
import com.example.demo.domain.Restaurant
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "restaurant",
    indexes = [
        Index(name = "idx_restaurant_orderId", columnList = "orderId"),
    ]
)
data class RestaurantEntity(

    @Column
    val name: String,

    @Column
    val orderId: Long,

    @Embedded
    val address: Address,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {

    fun toDomainModel(): Restaurant {
        return Restaurant.of(
            name = name,
            orderId = orderId,
            address = address,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
        )
    }

    companion object {
        fun from(restaurant: Restaurant): RestaurantEntity {
            return RestaurantEntity(
                name = restaurant.name,
                orderId = restaurant.orderId,
                address = restaurant.address,
                id = restaurant.id,
            )
        }
    }
}
