package com.example.demo.adapter.entity

import com.example.demo.domain.Address
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "restaurant",
    indexes = [
        Index(name = "idx_restaurant_orderId", columnList = "orderId"),
    ]
)
data class RestaurantEntity(

    @Embedded
    val address: Address,

    @Column
    val name: Long,

    @Column
    val orderId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
