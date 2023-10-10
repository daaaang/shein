package com.order.adapter.entity

import com.order.domain.model.Order
import com.order.domain.model.StepStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Column
    val userId: Long,

    @Column
    @Enumerated(EnumType.STRING)
    val stepStatus: StepStatus,

    @Column
    val txId: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {

    fun toDomainModel(): Order {
        return Order(
            userId = userId,
            stepStatus = stepStatus,
            txId = txId,
            id = id,
        )
    }

    companion object {
        fun fromDomainModel(order: Order): OrderEntity {
            return OrderEntity(
                userId = order.userId,
                stepStatus = order.stepStatus,
                txId = order.txId,
                id = order.id,
            )
        }
    }
}
