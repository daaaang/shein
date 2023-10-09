package com.order.adapter.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderProductEntityRepository : JpaRepository<OrderProductEntity, Long> {

    fun findOrderProductEntitiesByOrderId(orderId: Long): List<OrderProductEntity>
}