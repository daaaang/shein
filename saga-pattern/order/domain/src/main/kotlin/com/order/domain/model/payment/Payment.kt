package com.order.domain.model.payment

import com.fasterxml.jackson.annotation.JsonProperty

data class Payment(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("totalPrice") val totalPrice: Double,
)