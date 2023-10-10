package com.order.api.orders

import com.fasterxml.jackson.annotation.JsonProperty
import com.order.application.service.OrderCreationService
import com.order.application.service.OrderService
import com.order.application.service.TestService
import com.order.domain.model.OrderProduct
import com.order.domain.model.OrderRequest
import com.order.domain.model.ProductItem
import com.order.domain.share.Logger
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
    private val orderCreationService: OrderCreationService,
    private val testService: TestService,
) {

    @GetMapping("/test")
    fun runTest(): ResponseEntity<Unit> {
        log.info("/order/test api request")
        testService.runEvent()

        return ResponseEntity.ok().build()
    }

    @PostMapping("/create")
    fun createOrder(
        @RequestBody orderApiRequest: OrderApiRequest,
    ): ResponseEntity<Unit> {
        orderCreationService.createOrder(orderApiRequest.toOrderRequest())

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }


    data class OrderApiRequest(
        @JsonProperty("userId") val userId: Long,
        @JsonProperty("orderProducts") val productItems: List<ProductItemApiRequest>,
    ) {
        fun toOrderRequest(): OrderRequest {
            return OrderRequest(
                userId = userId,
                productItems = productItems.map {
                    ProductItem(
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

    companion object : Logger()
}