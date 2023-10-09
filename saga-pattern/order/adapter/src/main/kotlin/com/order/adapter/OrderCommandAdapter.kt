package com.order.adapter

import com.order.adapter.entity.OrderEntity
import com.order.adapter.entity.OrderEntityRepository
import com.order.adapter.entity.OrderProductEntity
import com.order.adapter.entity.OrderProductEntityRepository
import com.order.domain.usecase.OrderUseCase
import com.order.domain.model.Order
import com.order.domain.model.OrderItems
import com.order.domain.model.OrderProduct
import org.springframework.stereotype.Component

@Component
class OrderCommandAdapter(
    private val orderEntityRepository: OrderEntityRepository,
    private val orderProductEntityRepository: OrderProductEntityRepository,
) : OrderUseCase {

    override fun saveOrder(order: Order): Order {
        return orderEntityRepository.save(OrderEntity.fromDomainModel(order))
            .toDomainModel()
    }

    override fun saveOrderProduct(orderProducts: OrderItems): OrderItems {
        val orderProductsEntities = orderProductEntityRepository.saveAll(orderProducts.orderProducts.map {
            OrderProductEntity(
                amount = it.amount,
                productId = it.productId,
                orderId = orderProducts.orderId,
            )
        })

        return OrderItems(
            orderId = orderProducts.orderId,
            orderProductsEntities.map {
                OrderProduct(
                    amount = it.amount,
                    productId = it.productId,
                )
            }
        )
    }

    override fun rejectOrder(order: Order) {
        TODO("Not yet implemented")
    }
}