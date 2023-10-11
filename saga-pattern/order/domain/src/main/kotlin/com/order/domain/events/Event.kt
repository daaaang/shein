package com.order.domain.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.order.domain.model.ProductItem
import com.order.domain.model.ProductPrice
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.Payment
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.model.user.UserStatusType

@JsonPropertyOrder(value = ["eventType", "txId"])
sealed class Event @JsonCreator constructor(
    @JsonProperty("eventType") open val eventType: String?,
    @JsonProperty("txId") open val txId: String,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = OrderKitchenTicketCreationConsumeEvent::class, name = "OrderKitchenTicketCreationConsumeEvent"),
    JsonSubTypes.Type(value = OrderPaymentStatusConsumeEvent::class, name = "OrderPaymentStatusConsumeEvent"),
    JsonSubTypes.Type(value = OrderKitchenTicketStatusConsumeEvent::class, name = "OrderKitchenTicketStatusConsumeEvent"),
    JsonSubTypes.Type(value = UserStatusConsumeEvent::class, name = "UserStatusConsumeEvent"),
)
@JsonPropertyOrder(value = ["eventType", "txId"])
sealed class OrderConsumeEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String?,
    @JsonProperty("txId") override val txId: String,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "userId"])
data class OrderUserPublishEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderUserPublishEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("userId") val userId: Long,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "payment"])
data class OrderPaymentCreationPublishEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderPaymentCreationPublishEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("payment") val payment: Payment,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "productItems"])
data class OrderKitchenCreationPublishEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderKitchenCreationPublishEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("productItems") val productItems: List<ProductItem>,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "paymentStatus"])
data class OrderPaymentStatusPublishEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderPaymentStatusPublishEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("paymentStatus") val paymentStatus: PaymentStatusType,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "kitchenStatus"])
data class OrderKitchenTicketStatusUpdatePublishEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderKitchenTicketStatusUpdatePublishEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("kitchenStatus") val kitchenStatus: KitchenTicketStatusType,
) : Event(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "kitchenStatus", "productPrices"])
data class OrderKitchenTicketCreationConsumeEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderKitchenTicketCreationConsumeEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("kitchenStatus") val kitchenStatus: KitchenTicketCreationType,
    @JsonProperty("productPrices") val productPrices: List<ProductPrice>,
) : OrderConsumeEvent(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "paymentStatus"])
data class OrderPaymentStatusConsumeEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderPaymentStatusConsumeEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("paymentStatus") val paymentStatus: PaymentStatusType,
) : OrderConsumeEvent(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "orderId", "kitchenStatus"])
data class OrderKitchenTicketStatusConsumeEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "OrderKitchenTicketStatusConsumeEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("orderId") val orderId: Long,
    @JsonProperty("kitchenStatus") val kitchenStatus: KitchenTicketStatusType,
) : OrderConsumeEvent(eventType = eventType, txId = txId)

@JsonPropertyOrder(value = ["eventType", "txId", "userId", "userStatus"])
data class UserStatusConsumeEvent @JsonCreator constructor(
    @JsonProperty("eventType") override val eventType: String? = "UserStatusConsumeEvent",
    @JsonProperty("txId") override val txId: String,
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("userStatus") val userStatus: UserStatusType,
) : OrderConsumeEvent(eventType = eventType, txId = txId)
