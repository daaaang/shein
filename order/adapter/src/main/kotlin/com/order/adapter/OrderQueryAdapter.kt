package com.order.adapter

import com.order.adapter.entity.OrderEntityRepository
import com.order.application.port.OrderQueryPort
import com.order.domain.model.Order
import org.springframework.stereotype.Component

@Component
class OrderQueryAdapter(
    private val orderEntityRepository: OrderEntityRepository,
) : OrderQueryPort {
    override fun getOrderByTxId(txId: String): Order {
        return orderEntityRepository.findOrderEntityByTxId(txId)
            ?.toDomainModel() ?: throw IllegalArgumentException()
    }
}