package com.order.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductPrice(
    @JsonProperty("productId") val productId: Long,
    @JsonProperty("price") val price: Double,
)