package com.example.demo.adapter.entity

import com.example.demo.domain.Order
import com.example.demo.domain.PaymentInfo
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
    name = "orders",
    indexes = [
        Index(name = "idx_order_userId", columnList = "userId"),
    ]
)
data class OrderEntity(

    @Embedded
    val paymentInfo: PaymentInfo,

    @Column
    val restaurantId: Long,

    @Column
    val userId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {

    fun toDomainModel(): Order {
        return Order.of(
            paymentInfo = paymentInfo,
            restaurantId = restaurantId,
            userId = userId,
            id = id,
        )
    }

    companion object {
        fun from(order: Order): OrderEntity {
            return OrderEntity(
                paymentInfo = order.paymentInfo,
                restaurantId = order.restaurantId,
                userId = order.userId,
                id = order.id,
            )
        }
    }


}