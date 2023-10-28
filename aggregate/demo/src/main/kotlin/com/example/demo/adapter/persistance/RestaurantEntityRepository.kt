package com.example.demo.adapter.persistance

import com.example.demo.adapter.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RestaurantEntityRepository : JpaRepository<RestaurantEntity, Long>