package com.order.adapter

import com.order.adapter.entity.OrderProductEntity
import com.order.adapter.entity.OrderProductEntityRepository
import com.order.application.port.OrderProductCommandPort
import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
class OrderProductCommandAdapter(
    private val orderProductEntityRepository: OrderProductEntityRepository,
) : OrderProductCommandPort {

    override fun saveOrderProduct(orderProduct: OrderProduct): OrderProduct {
        return orderProductEntityRepository.save(
            OrderProductEntity.fromDomainModel(orderProduct)
        ).toDomainModel()
    }

    override fun saveOrderProducts(orderProducts: List<OrderProduct>): List<OrderProduct> {
        return orderProductEntityRepository.saveAll(
            orderProducts.map {
                OrderProductEntity.fromDomainModel(it)
            }
        ).map { it.toDomainModel() }
    }
}