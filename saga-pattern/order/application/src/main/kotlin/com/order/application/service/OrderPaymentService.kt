package com.order.application.service

import com.order.application.port.OrderProductQueryPort
import com.order.application.port.OrderQueryPort
import com.order.domain.events.OrderPaymentEvent
import com.order.domain.model.Payment
import com.order.domain.model.ProductPrice
import com.order.domain.usecase.OrderPaymentUseCase
import org.springframework.stereotype.Component

@Component
class OrderPaymentService(
    private val orderQueryPort: OrderQueryPort,
    private val orderProductQueryPort: OrderProductQueryPort,
) : OrderPaymentUseCase {
    override fun createPaymentCreditEvent(
        txId: String,
        orderId: Long,
        productPrices: List<ProductPrice>
    ): OrderPaymentEvent {

        val order = orderQueryPort.getOrderByOrderId(orderId)
        val orderProducts = orderProductQueryPort.getOrderProductByOrderId(order.id)

        val productPricesMap = productPrices.associateBy { it.productId }

        val totalPrice = orderProducts.sumOf {
            it.amount * (productPricesMap[it.productId]?.price ?: throw IllegalArgumentException())
        }

        return OrderPaymentEvent(
            txId = txId,
            orderId = order.id,
            payment = Payment(
                userId = order.userId,
                totalPrice = totalPrice,
            ),
        )
    }
}