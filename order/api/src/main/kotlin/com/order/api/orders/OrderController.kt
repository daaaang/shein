package com.order.api.orders

import com.fasterxml.jackson.annotation.JsonProperty
import com.order.application.service.OrderService
import com.order.application.service.TestService
import com.order.domain.model.OrderProduct
import com.order.domain.model.OrderRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val testService: TestService,
) {

    @GetMapping("/test")
    fun runTest(): ResponseEntity<Unit> {
        testService.runEvent()

        return ResponseEntity.ok().build()
    }

    @PostMapping("/create")
    fun createOrder(
        @RequestBody orderApiRequest: OrderApiRequest,
    ): ResponseEntity<Unit> {
        orderService.createOrder(orderApiRequest.toOrderRequest())

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }


    data class OrderApiRequest(
        @JsonProperty("userId") val userId: Long,
        @JsonProperty("orderProducts") val orderProducts: List<ProductItemApiRequest>,
    ) {
        fun toOrderRequest(): OrderRequest {
            return OrderRequest(
                userId = userId,
                orderProducts = orderProducts.map {
                    OrderProduct(
                        productId = it.productId,
                        amount = it.amount,
                    )
                }
            )
        }
    }

    data class ProductItemApiRequest(
        @JsonProperty("productId") val productId: Long,
        @JsonProperty("amount") val amount: Long,
    )
}