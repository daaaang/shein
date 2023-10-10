package com.order.domain.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.model.ProductItem
import com.order.domain.model.payment.Payment
import com.order.domain.model.ProductPrice
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.model.user.UserStatusType

sealed interface OrderPublishEvent

sealed interface OrderConsumeEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = OrderUserPublishEvent::class, name = "OrderUserPublishEvent"),
    JsonSubTypes.Type(value = OrderPaymentCreationPublishEvent::class, name = "OrderPaymentCreationPublishEvent"),
    JsonSubTypes.Type(value = OrderKitchenCreationPublishEvent::class, name = "OrderKitchenCreationPublishEvent"),
    JsonSubTypes.Type(value = OrderPaymentStatusPublishEvent::class, name = "OrderPaymentStatusPublishEvent"),
    JsonSubTypes.Type(value = OrderKitchenStatusUpdatePublishEvent::class, name = "OrderKitchenStatusUpdatePublishEvent"),
    JsonSubTypes.Type(value = OrderKitchenTicketCreationConsumeEvent::class, name = "OrderKitchenTicketCreationConsumeEvent"),
    JsonSubTypes.Type(value = OrderPaymentStatusConsumeEvent::class, name = "OrderPaymentStatusConsumeEvent"),
    JsonSubTypes.Type(value = OrderKitchenStatusConsumeEvent::class, name = "OrderKitchenStatusConsumeEvent"),
    JsonSubTypes.Type(value = UserStatusConsumeEvent::class, name = "UserStatusConsumeEvent"),
)
sealed class Event(
    open val txId: String,
)

data class OrderUserPublishEvent(
    override val txId: String,
    val userId: Long,
) : Event(txId = txId), OrderPublishEvent

data class OrderPaymentCreationPublishEvent(
    override val txId: String,
    val orderId: Long,
    val payment: Payment,
) : Event(txId = txId), OrderPublishEvent

data class OrderKitchenCreationPublishEvent(
    override val txId: String,
    val orderId: Long,
    val productItems: List<ProductItem>,
) : Event(txId = txId), OrderPublishEvent

data class OrderPaymentStatusPublishEvent(
    override val txId: String,
    val orderId: Long,
    val paymentStatus: PaymentStatusType,
) : Event(txId = txId), OrderPublishEvent

data class OrderKitchenStatusUpdatePublishEvent(
    override val txId: String,
    val orderId: Long,
    val kitchenStatus: KitchenTicketStatusType,
) : Event(txId = txId), OrderPublishEvent

data class OrderKitchenTicketCreationConsumeEvent(
    override val txId: String,
    val orderId: Long,
    val kitchenStatus: KitchenTicketCreationType,
    val productPrices: List<ProductPrice>,
) : Event(txId = txId), OrderConsumeEvent

data class OrderPaymentStatusConsumeEvent(
    override val txId: String,
    val orderId: Long,
    val paymentStatus: PaymentStatusType,
) : Event(txId = txId), OrderConsumeEvent

data class OrderKitchenStatusConsumeEvent(
    override val txId: String,
    val orderId: Long,
    val kitchenStatus: KitchenTicketStatusType,
) : Event(txId = txId), OrderConsumeEvent

data class UserStatusConsumeEvent(
    override val txId: String,
    val userId: Long,
    val userStatus: UserStatusType,
) : Event(txId = txId), OrderConsumeEvent
