package com.order.adapter

import com.order.adapter.entity.OrderProductEntityRepository
import com.order.application.port.OrderProductQueryPort
import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
class OrderProductQueryAdapter(
    private val orderProductEntityRepository: OrderProductEntityRepository,
) : OrderProductQueryPort {
    override fun getOrderProductByOrderId(orderId: Long): List<OrderProduct> {
        return orderProductEntityRepository.findOrderProductEntitiesByOrderId(orderId)
            .map {
                it.toDomainModel()
            }
    }
}