package com.order.domain.events.dispatcher

import com.order.domain.events.ErrorEventMessage
import com.order.domain.events.OrderKitchenStatusConsumeEvent
import com.order.domain.events.OrderKitchenTicketCreationConsumeEvent
import com.order.domain.events.OrderPaymentStatusConsumeEvent
import com.order.domain.events.TargetEventMessage
import com.order.domain.events.UserStatusConsumeEvent
import com.order.domain.events.handler.OrderKitchenTicketCreationHandler
import com.order.domain.events.handler.OrderKitchenTicketStatusHandler
import com.order.domain.events.handler.OrderPaymentCreationEventHandler
import com.order.domain.events.handler.OrderStatusEventHandler
import com.order.domain.events.publisher.EventTarget
import com.order.domain.model.kitchen.KitchenTicketCreationType
import com.order.domain.model.kitchen.KitchenTicketStatusType
import com.order.domain.model.payment.PaymentStatusType
import com.order.domain.model.user.UserStatusType
import com.order.domain.usecase.OrderUseCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class EventConsumeDispatcherTest(
    private val orderKitchenTicketCreationHandler: OrderKitchenTicketCreationHandler = mockk(),
    private val orderKitchenTicketStatusHandler: OrderKitchenTicketStatusHandler = mockk(),
    private val orderPaymentCreationEventHandler: OrderPaymentCreationEventHandler = mockk(),
    private val orderStatusEventHandler: OrderStatusEventHandler = mockk(),
    private val orderUseCase: OrderUseCase = mockk(),
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val sut = EventConsumeDispatcher(
        orderKitchenTicketCreationHandler = orderKitchenTicketCreationHandler,
        orderKitchenTicketStatusHandler = orderKitchenTicketStatusHandler,
        orderPaymentCreationEventHandler = orderPaymentCreationEventHandler,
        orderStatusEventHandler = orderStatusEventHandler,
        orderUseCase = orderUseCase,
    )

    given("각 핸들러의 성공과 실패에 대한 처리 결과가 주어져요") {

        coEvery { orderKitchenTicketCreationHandler.process(any()) } just Runs
        coEvery { orderKitchenTicketStatusHandler.process(any()) } just Runs
        coEvery { orderPaymentCreationEventHandler.process(any()) } just Runs
        coEvery { orderStatusEventHandler.process(any()) } just Runs

        coEvery { orderUseCase.rejectOrder(any(), any()) } just Runs
        coEvery { orderKitchenTicketCreationHandler.reject(any(), any()) } just Runs
        coEvery { orderPaymentCreationEventHandler.reject(any(), any()) } just Runs

        `when`("event가 TargetEventMessage<UserStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val event = UserStatusConsumeEvent(
                txId = txId,
                userId = 10L,
                userStatus = UserStatusType.NOMAL,
            )

            val message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                message = event,
            )

            sut.dispatch(message = message, clazz = UserStatusConsumeEvent::class)

            then("orderKitchenTicketCreationHandler process만 동작을 해야해요.") {
                coVerify(exactly = 1) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }


        `when`("event가 TargetEventMessage<OrderKitchenTicketCreationConsumeEvent>일 떄,") {
            val txId = "1234"

            val event = OrderKitchenTicketCreationConsumeEvent(
                txId = txId,
                orderId = 1L,
                kitchenStatus = KitchenTicketCreationType.APPROVAL,
                productPrices = listOf()
            )

            val message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                message = event,
            )

            sut.dispatch(message = message, clazz = OrderKitchenTicketCreationConsumeEvent::class)

            then("orderPaymentCreationEventHandler process만 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 1) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 TargetEventMessage<OrderPaymentStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val event = OrderPaymentStatusConsumeEvent(
                txId = txId,
                orderId = 1L,
                paymentStatus = PaymentStatusType.APPROVAL,
            )

            val message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                message = event,
            )

            sut.dispatch(message = message, clazz = OrderPaymentStatusConsumeEvent::class)

            then("orderKitchenTicketStatusHandler process만 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 1) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 TargetEventMessage<OrderKitchenStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val event = OrderKitchenStatusConsumeEvent(
                txId = txId,
                orderId = 1L,
                kitchenStatus = KitchenTicketStatusType.APPROVAL,
            )

            val message = TargetEventMessage(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                message = event,
            )

            sut.dispatch(message = message, clazz = OrderKitchenStatusConsumeEvent::class)

            then("orderKitchenTicketStatusHandler process만 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 1) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 ErrorEventMessage<UserStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val message = ErrorEventMessage<UserStatusConsumeEvent>(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                errorMessage = "Error",
            )

            sut.dispatch(message = message, clazz = UserStatusConsumeEvent::class)

            then("orderUseCase rejectOrder 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 1) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 ErrorMessage<OrderKitchenTicketCreationConsumeEvent>일 떄,") {
            val txId = "1234"

            val message = ErrorEventMessage<OrderKitchenTicketCreationConsumeEvent>(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                errorMessage = "rejected"
            )

            sut.dispatch(message = message, clazz = OrderKitchenTicketCreationConsumeEvent::class)

            then("orderUseCase rejectOrder 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 1) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 ErrorEventMessage<OrderPaymentStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val message = ErrorEventMessage<OrderPaymentStatusConsumeEvent>(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                errorMessage = "error",
            )

            sut.dispatch(message = message, clazz = OrderPaymentStatusConsumeEvent::class)

            then("orderKitchenTicketCreationHandler reject 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 1) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }

        `when`("event가 ErrorEventMessage<OrderKitchenStatusConsumeEvent>일 떄,") {
            val txId = "1234"

            val message = ErrorEventMessage<OrderKitchenStatusConsumeEvent>(
                target = EventTarget.ORDER_CREATION,
                txId = txId,
                errorMessage = "Error",
            )

            sut.dispatch(message = message, clazz = OrderKitchenStatusConsumeEvent::class)

            then("orderKitchenTicketStatusHandler 핸들러만 동작을 해야해요.") {
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.process(any()) }
                coVerify(exactly = 0) { orderPaymentCreationEventHandler.process(any()) }
                coVerify(exactly = 0) { orderKitchenTicketStatusHandler.process(any()) }
                coVerify(exactly = 0) { orderStatusEventHandler.process(any()) }

                coVerify(exactly = 0) { orderUseCase.rejectOrder(any(), any()) }
                coVerify(exactly = 0) { orderKitchenTicketCreationHandler.reject(any(), any()) }
                coVerify(exactly = 1) { orderPaymentCreationEventHandler.reject(any(), any()) }
            }
        }
    }


})
