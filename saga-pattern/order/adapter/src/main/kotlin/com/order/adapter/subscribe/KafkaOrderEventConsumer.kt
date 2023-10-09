package com.order.adapter.subscribe

import com.order.domain.events.EventMessage
import com.order.domain.events.consumer.OrderEventConsumer
import com.order.domain.events.handler.EventDispatcher
import com.order.domain.model.KitchenOrder
import com.order.domain.model.KitchenOrderTicket
import com.order.domain.model.OrderRejectReason
import com.order.domain.model.PaymentStatus
import com.order.domain.model.UserStatus
import com.order.domain.model.UserStatusType
import com.order.domain.share.Logger
import com.order.domain.usecase.OrderKitchenUseCase
import com.order.domain.usecase.OrderPaymentUseCase
import com.order.domain.usecase.OrderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaOrderEventConsumer(
    private val eventDispatcher: EventDispatcher,
    private val orderUseCase: OrderUseCase,
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

            when (message) {
                is EventMessage.SuccessEventMessage<UserStatus> -> {
                    val userStatus = message.message
                    when (userStatus.userStatus) {
                        UserStatusType.NOMAL -> {
                            eventDispatcher.dispatch(
                                event = orderKitchenUseCase.createOrderKitchenEvent(txId = message.txId)
                            )
                        }

                        UserStatusType.ABNOMAL -> {
                            orderUseCase.rejectOrder(
                                txId = message.txId,
                                orderRejectReason = OrderRejectReason.USER_ABNOMAL.name
                            )
                        }
                    }
                }

                is EventMessage.FailEventMessage<UserStatus> -> {
                    orderUseCase.rejectOrder(
                        txId = message.txId,
                        orderRejectReason = message.errorMessage
                    )
                }
            }
        }
    }

    @KafkaListener(topics = ["kitchen-to-order"], groupId = "saga")
    override fun consumeKitchenTicket(message: EventMessage<KitchenOrder>) {

        coroutineScope.launch {
            when (message) {
                is EventMessage.SuccessEventMessage<KitchenOrder> -> {
                    when (val kitchenOrder = message.message) {
                        is KitchenOrderTicket -> eventDispatcher.dispatch(
                            orderPaymentUseCase.createPaymentCreditEvent(
                                txId = kitchenOrder.txId,
                                orderId = kitchenOrder.orderId,
                                productPrices = kitchenOrder.productPrices,
                            )
                        )
                    }
                }

                is EventMessage.FailEventMessage<KitchenOrder> -> {
                    TODO("보상 트랜잭션")
                }
            }
        }
    }

    @KafkaListener(topics = ["payment-to-order"], groupId = "saga")
    override fun consumePayment(message: EventMessage<PaymentStatus>) {

        coroutineScope.launch {
            when (message) {
                is EventMessage.SuccessEventMessage<PaymentStatus> -> {
                    eventDispatcher.dispatch(
                        TODO("")
                    )
                }

                is EventMessage.FailEventMessage<PaymentStatus> -> {
                    eventDispatcher.dispatch(
                        TODO("")
                    )
                }
            }
        }
    }

    companion object : Logger()
}