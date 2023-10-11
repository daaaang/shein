package com.order.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.order.domain.events.ErrorEventMessage
import com.order.domain.events.EventMessage
import com.order.domain.events.OrderKitchenCreationPublishEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderKitchenTicketStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketStatusUpdatePublishEvent
import com.order.domain.events.OrderPaymentCreationPublishEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.OrderPaymentStatusPublishEvent
import com.order.domain.events.OrderUserPublishEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.ProductPrice
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.model.user.UserStatusType
import com.order.domain.share.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class TestKafkaConsumer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val coroutineScope: CoroutineScope,
    @Qualifier("eventObjectMapper") private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["order-to-user-status"], groupId = "saga")
    fun consumeUserStatusEvent(@Payload message: EventMessage<OrderUserPublishEvent>) {
        log.info("message consume = $message")

        coroutineScope.launch {
            when (message) {
                is TargetEventMessage -> {
                    val orderUserPublishEvent = objectMapper.convertValue(message.message, OrderUserPublishEvent::class.java)

                    log.info("orderUserPublish Event = $orderUserPublishEvent")
                    when {
                        orderUserPublishEvent.userId % 3 == 0L -> {
                            kafkaTemplate.send(
                                EventConsumeName.USER_TO_ORDER_STATUS.topicName,
                                TargetEventMessage(
                                    target = EventTarget.ORDER_CREATION,
                                    txId = orderUserPublishEvent.txId,
                                    message = UserStatusConsumeEvent(
                                        txId = orderUserPublishEvent.txId,
                                        userId = orderUserPublishEvent.userId,
                                        userStatus = UserStatusType.ABNOMAL,
                                    )
                                )
                            )
                        }

                        else -> {
                            kafkaTemplate.send(
                                EventConsumeName.USER_TO_ORDER_STATUS.topicName,
                                TargetEventMessage(
                                    target = EventTarget.ORDER_CREATION,
                                    txId = orderUserPublishEvent.txId,
                                    message = UserStatusConsumeEvent(
                                        txId = orderUserPublishEvent.txId,
                                        userId = orderUserPublishEvent.userId,
                                        userStatus = UserStatusType.NOMAL,
                                    )
                                )
                            )
                        }
                    }
                }

                is ErrorEventMessage -> {
                    kafkaTemplate.send(
                        EventConsumeName.USER_TO_ORDER_STATUS.topicName,
                        ErrorEventMessage<UserStatusConsumeEvent>(
                            target = EventTarget.ORDER_CREATION,
                            txId = message.txId,
                            errorMessage = "error",
                        )
                    )
                }
            }
        }
    }

    @KafkaListener(topics = ["order-to-kitchen-ticket-creation"], groupId = "saga")
    fun consumeKitchenTicketCreation(@Payload message: EventMessage<OrderKitchenCreationPublishEvent>) {
        log.info("message consume = $message")

        when (message) {
            is TargetEventMessage -> {
                val orderKitchenCreationPublishEvent = objectMapper.convertValue(message.message, OrderKitchenCreationPublishEvent::class.java)

                when {
                    orderKitchenCreationPublishEvent.productItems.none { it.productId == 1L } -> {
                        kafkaTemplate.send(
                            EventConsumeName.KITCHEN_TO_ORDER_TICKET_CREATION.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderKitchenCreationPublishEvent.txId,
                                message = OrderKitchenTicketCreationConsumeEvent(
                                    txId = orderKitchenCreationPublishEvent.txId,
                                    orderId = orderKitchenCreationPublishEvent.orderId,
                                    kitchenStatus = KitchenTicketCreationType.PENDING,
                                    productPrices = orderKitchenCreationPublishEvent.productItems.map {
                                        ProductPrice(
                                            productId = it.productId,
                                            price = 100.0,
                                        )
                                    }
                                )
                            )
                        )
                    }


                    else -> {
                        kafkaTemplate.send(
                            EventConsumeName.KITCHEN_TO_ORDER_TICKET_CREATION.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderKitchenCreationPublishEvent.txId,
                                message = OrderKitchenTicketCreationConsumeEvent(
                                    txId = orderKitchenCreationPublishEvent.txId,
                                    orderId = orderKitchenCreationPublishEvent.orderId,
                                    kitchenStatus = KitchenTicketCreationType.REJECTED_FULL_WAITING,
                                    productPrices = listOf(),
                                )
                            )
                        )
                    }
                }
            }

            is ErrorEventMessage -> {
                kafkaTemplate.send(
                    EventConsumeName.KITCHEN_TO_ORDER_TICKET_CREATION.topicName,
                    ErrorEventMessage<OrderKitchenTicketCreationConsumeEvent>(
                        target = EventTarget.ORDER_CREATION,
                        txId = message.txId,
                        errorMessage = "error",
                    )
                )
            }
        }
    }

    @KafkaListener(topics = ["order-to-payment-pay"], groupId = "saga")
    fun consumePaymentCreation(@Payload message: EventMessage<OrderPaymentCreationPublishEvent>) {
        log.info("message consume = $message")

        when (message) {
            is TargetEventMessage -> {
                val orderPaymentCreationPublishEvent = objectMapper.convertValue(message.message, OrderPaymentCreationPublishEvent::class.java)

                when {
                    orderPaymentCreationPublishEvent.orderId % 2 == 0L -> {
                        kafkaTemplate.send(
                            EventConsumeName.PAYMENT_TO_ORDER_PAY.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderPaymentCreationPublishEvent.txId,
                                message = OrderPaymentStatusConsumeEvent(
                                    txId = orderPaymentCreationPublishEvent.txId,
                                    orderId = orderPaymentCreationPublishEvent.orderId,
                                    paymentStatus = PaymentStatusType.APPROVAL,
                                )
                            )
                        )
                    }

                    else -> {
                        kafkaTemplate.send(
                            EventConsumeName.PAYMENT_TO_ORDER_PAY.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderPaymentCreationPublishEvent.txId,
                                message = OrderPaymentStatusConsumeEvent(
                                    txId = orderPaymentCreationPublishEvent.txId,
                                    orderId = orderPaymentCreationPublishEvent.orderId,
                                    paymentStatus = PaymentStatusType.REJECT_DURING_PAYMENT,
                                )
                            )
                        )
                    }
                }
            }

            is ErrorEventMessage -> {
                kafkaTemplate.send(
                    EventConsumeName.PAYMENT_TO_ORDER_PAY.topicName,
                    ErrorEventMessage<OrderPaymentCreationPublishEvent>(
                        target = EventTarget.ORDER_CREATION,
                        txId = message.txId,
                        errorMessage = "error",
                    )
                )
            }
        }
    }

    @KafkaListener(topics = ["order-to-kitchen-ticket-status"], groupId = "saga")
    fun consumeKitchenTicketStatus(@Payload message: EventMessage<OrderKitchenTicketStatusUpdatePublishEvent>) {
        log.info("message consume = $message")

        when (message) {
            is TargetEventMessage -> {
                val orderKitchenTicketStatusUpdatePublishEvent = objectMapper.convertValue(message.message, OrderKitchenTicketStatusUpdatePublishEvent::class.java)
                log.info("orderKitchenTicketStatusUpdatePublishEvent = $orderKitchenTicketStatusUpdatePublishEvent")
                when {
                    orderKitchenTicketStatusUpdatePublishEvent.orderId % 3 == 0L -> {
                        kafkaTemplate.send(
                            EventConsumeName.KITCHEN_TO_ORDER_TICKET_STATUS.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderKitchenTicketStatusUpdatePublishEvent.txId,
                                message = OrderKitchenTicketStatusConsumeEvent(
                                    txId = orderKitchenTicketStatusUpdatePublishEvent.txId,
                                    orderId = orderKitchenTicketStatusUpdatePublishEvent.orderId,
                                    kitchenStatus = KitchenTicketStatusType.APPROVAL
                                )
                            )
                        )
                    }

                    else -> {
                        kafkaTemplate.send(
                            EventConsumeName.KITCHEN_TO_ORDER_TICKET_STATUS.topicName,
                            TargetEventMessage(
                                target = EventTarget.ORDER_CREATION,
                                txId = orderKitchenTicketStatusUpdatePublishEvent.txId,
                                message = OrderKitchenTicketStatusConsumeEvent(
                                    txId = orderKitchenTicketStatusUpdatePublishEvent.txId,
                                    orderId = orderKitchenTicketStatusUpdatePublishEvent.orderId,
                                    kitchenStatus = KitchenTicketStatusType.REJECT
                                )
                            )
                        )
                    }
                }
            }

            is ErrorEventMessage -> {
                kafkaTemplate.send(
                    EventConsumeName.KITCHEN_TO_ORDER_TICKET_STATUS.topicName,
                    ErrorEventMessage<OrderKitchenTicketStatusConsumeEvent>(
                        target = EventTarget.ORDER_CREATION,
                        txId = message.txId,
                        errorMessage = "error",
                    )
                )
            }
        }
    }

    @KafkaListener(topics = ["order-to-payment-pay-status"], groupId = "saga")
    fun consumePaymentStatus(@Payload message: EventMessage<OrderPaymentStatusPublishEvent>) {
        log.info("message consume = $message")

        when (message) {
            is TargetEventMessage -> {
                val orderPaymentStatusPublishEvent = objectMapper.convertValue(message.message, OrderPaymentStatusPublishEvent::class.java)
                log.info("orderPaymentStatusPublishEvent = $orderPaymentStatusPublishEvent")

                kafkaTemplate.send(
                    EventConsumeName.PAYMENT_TO_ORDER_PAY.topicName,
                    TargetEventMessage(
                        target = EventTarget.ORDER_CREATION,
                        txId = orderPaymentStatusPublishEvent.txId,
                        message = OrderPaymentStatusConsumeEvent(
                            txId = orderPaymentStatusPublishEvent.txId,
                            orderId = orderPaymentStatusPublishEvent.orderId,
                            paymentStatus = PaymentStatusType.REJECT_AFTER_PAYMENT,
                        )
                    )
                )
            }

            is ErrorEventMessage -> {
                kafkaTemplate.send(
                    EventConsumeName.PAYMENT_TO_ORDER_PAY.topicName,
                    ErrorEventMessage<OrderPaymentStatusConsumeEvent>(
                        target = EventTarget.ORDER_CREATION,
                        txId = message.txId,
                        errorMessage = "error",
                    )
                )
            }
        }
    }

    enum class EventConsumeName(
        val topicName: String,
    ) {
        USER_TO_ORDER_STATUS("user-to-order-status"),
        KITCHEN_TO_ORDER_TICKET_CREATION("kitchen-to-order-ticket-creation"),
        PAYMENT_TO_ORDER_PAY("payment-to-order-pay"),
        KITCHEN_TO_ORDER_TICKET_STATUS("kitchen-to-order-ticket-status"),
    }

    companion object : Logger()

}