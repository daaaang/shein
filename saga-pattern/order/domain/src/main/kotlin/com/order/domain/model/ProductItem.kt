package com.order.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductItem(
    @JsonProperty("productId") val productId: Long,
    @JsonProperty("amount") val amount: Long,
)