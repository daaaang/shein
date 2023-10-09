package com.order.adapter.subscribe

import com.order.domain.events.EventMessage
import com.order.domain.events.consumer.OrderEventConsumer
import com.order.domain.events.handler.EventDispatcher
import com.order.domain.events.publisher.EventStatus
import com.order.domain.model.KitchenOrder
import com.order.domain.model.KitchenOrderTicket
import com.order.domain.model.PaymentStatus
import com.order.domain.model.UserStatus
import com.order.domain.model.UserStatusType
import com.order.domain.share.Logger
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderPaymentUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaOrderEventConsumer(
    private val eventDispatcher: EventDispatcher,
    private val orderKitchenUseCase: OrderKitchenUseCase,
    private val orderPaymentUseCase: OrderPaymentUseCase,
    private val coroutineScope: CoroutineScope,
) : OrderEventConsumer {

    @KafkaListener(topics = ["order-to-test"], groupId = "saga")
    override fun consumeTest(message: EventMessage<String>) {
        log.info("message = $message")
    }

    @KafkaListener(topics = ["user-to-order"], groupId = "saga")
    override fun consumeUserStatus(message: EventMessage<UserStatus>) {
        coroutineScope.launch {
            val userStatus = message.message
            when (message.status) {
                EventStatus.APPROVED -> {
                    when (userStatus.userStatus) {
                        UserStatusType.NOMAL -> {
                            eventDispatcher.dispatch(
                                event = orderKitchenUseCase.createOrderKitchenEvent(txId = message.txId)
                            )
                        }

                        UserStatusType.ABNOMAL -> {
                            TODO("주문 취소")
                        }
                    }
                }

                EventStatus.REJECTED -> eventDispatcher.dispatch(
                    TODO("주문 취소")
                )
            }
        }
    }

    @KafkaListener(topics = ["kitchen-to-order"], groupId = "saga")
    override fun consumeKitchenTicket(message: EventMessage<KitchenOrder>) {

        coroutineScope.launch {
            val kitchenOrder = message.message
            when (message.status) {
                EventStatus.APPROVED -> {
                    when (kitchenOrder) {
                        is KitchenOrderTicket -> eventDispatcher.dispatch(
                            orderPaymentUseCase.createPaymentCreditEvent(
                                txId = kitchenOrder.txId,
                                orderId = kitchenOrder.orderId,
                                productPrices = kitchenOrder.productPrices,
                            )
                        )
                    }
                }

                EventStatus.REJECTED -> {
                    when (kitchenOrder) {
                        is KitchenOrderTicket -> eventDispatcher.dispatch(
                            TODO("보상 트랜잭션")
                        )
                    }
                }
            }
        }
    }

    @KafkaListener(topics = ["payment-to-order"], groupId = "saga")
    override fun consumePayment(message: EventMessage<PaymentStatus>) {

        coroutineScope.launch {
            when (message.status) {
                EventStatus.APPROVED -> eventDispatcher.dispatch(
                    TODO("")
                )

                EventStatus.REJECTED -> eventDispatcher.dispatch(
                    TODO("보상 트랜잭션")
                )
            }
        }
    }

    companion object : Logger()
}