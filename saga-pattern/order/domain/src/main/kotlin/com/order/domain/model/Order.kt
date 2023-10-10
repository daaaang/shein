package com.order.domain.model

import java.util.*

data class Order(
    val userId: Long,
    val stepStatus: StepStatus,
    val txId: String,
    val id: Long = 0L,
) {
    companion object {
        fun fromOrderRequest(orderRequest: OrderRequest): Order {
            return Order(
                userId = orderRequest.userId,
                stepStatus = StepStatus.PENDING,
                txId = UUID.randomUUID().toString(),
            )
        }
    }

    fun updateOrderStatus(stepStatusToUpdate: StepStatus): Order {
        return Order(
            userId = userId,
            stepStatus = stepStatusToUpdate,
            txId = txId,
            id = id,
        )
    }
}