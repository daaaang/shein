package com.order.adapter.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderEntityRepository : JpaRepository<OrderEntity, Long>