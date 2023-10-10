package com.order.adapter

import com.order.adapter.entity.OrderEntity
import com.order.adapter.entity.OrderEntityRepository
import com.order.application.port.OrderCommandPort
import com.order.domain.model.Order
import org.springframework.stereotype.Component

@Component
class OrderCommandAdapter(
    private val orderEntityRepository: OrderEntityRepository,
) : OrderCommandPort {

    override fun saveOrder(order: Order): Order {
        return orderEntityRepository.save(OrderEntity.fromDomainModel(order))
            .toDomainModel()
    }
}