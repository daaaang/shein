package com.order.adapter.entity

import com.order.domain.model.OrderProduct
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "order_product")
data class OrderProductEntity(
    @Column
    val amount: Long,

    @Column
    val productId: Long,

    @Column
    val orderId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {

    fun toDomainModel(): OrderProduct {
        return OrderProduct(
            orderId = orderId,
            amount = amount,
            productId = productId,
        )
    }

    companion object {
        fun fromDomainModel(orderProduct: OrderProduct): OrderProductEntity {
            return OrderProductEntity(
                amount = orderProduct.amount,
                productId = orderProduct.productId,
                orderId = orderProduct.orderId,
                id = orderProduct.id,
            )
        }
    }

}
