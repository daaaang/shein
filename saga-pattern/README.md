# 사가 패턴 - 주문 로직 구현

## 1. 목표 와이어 프레임

![structure.png](image%2Fstructure.png)

- 유저가 주문을 요청에 대한 각 서비스를 사가 패턴으로 구현해요.
- 라이브러리 없이 직접 이벤트 발행 / 수신 구조로 사가 패턴을 구현해요.

## 2. 이벤트 발행 구조

![img.png](image/event.png)

## 3. 주문 요청 진행 과정

``` kotlin
    @KafkaListener(topics = ["user-to-order-status"], groupId = "saga")
    override fun consumeUserStatus(message: EventMessage<UserStatusConsumeEvent>) {
        val clazz = UserStatusConsumeEvent::class
        coroutineScope.launch {
            eventConsumeDispatcher.dispatch(message, clazz)
        }
    }
```
- EventConsumer는 메세지를 수신하고, EventConsumeDispatcher를 호출해요.

``` kotlin

    suspend fun dispatch(message: EventMessage<OrderConsumeEvent>, clazz: KClass<out OrderConsumeEvent>) {
        when (message) {
            is TargetEventMessage<OrderConsumeEvent> -> {
                when(val event = message.message) {
                    is UserStatusConsumeEvent -> orderKitchenTicketCreationHandler.process(event)
                    is OrderKitchenTicketCreationConsumeEvent -> orderPaymentCreationEventHandler.process(event)
                    is OrderPaymentStatusConsumeEvent -> orderKitchenTicketStatusHandler.process(event)
                    is OrderKitchenStatusConsumeEvent -> orderStatusEventHandler.process(event)
                }
            }

            is ErrorEventMessage<OrderConsumeEvent> -> {
                when(clazz) {
                    UserStatusConsumeEvent::class -> orderUseCase.rejectOrder(txId = message.txId, orderRejectReason = message.errorMessage)
                    OrderKitchenTicketCreationConsumeEvent::class -> orderUseCase.rejectOrder(txId = message.txId, orderRejectReason = message.errorMessage)
                    OrderPaymentStatusConsumeEvent::class -> orderKitchenTicketCreationHandler.reject(message.txId, rejectReason = message.errorMessage)
                    OrderKitchenStatusConsumeEvent::class -> orderPaymentCreationEventHandler.reject(message.txId, rejectReason = message.errorMessage)
                }
            }
        }
    }
```
- EventConsumeDispatcher는 수신한 이벤트 결과를 바탕으로 다음 이벤트 핸들러를 호출해요.  


``` kotlin
    override suspend fun process(event: UserStatusConsumeEvent) {

        when (event.userStatus) {
            UserStatusType.NOMAL -> {
                val orderKitchenTicketEventMessage= createOrderKitchenTicketEvent(txId = event.txId)
                eventPublisher.publish(
                    eventName = EventPublishName.ORDER_TO_KITCHEN_CREATION,
                    message = orderKitchenTicketEventMessage,
                )
            }

            UserStatusType.ABNOMAL -> {
                orderUseCase.rejectOrder(
                    txId = event.txId,
                    orderRejectReason = OrderRejectReason.USER_ABNOMAL.name
                )
            }
        }
    }
    
    private suspend fun createOrderKitchenTicketEvent(txId: String): EventMessage<Event> {
        return EventMessageCreator.createMessage(
            eventTarget = EventTarget.ORDER_CREATION,
            txId = txId,
            eventAction = {
                orderKitchenUseCase.createOrderKitchenEvent(txId = txId)
            }
        )
    }
```
- 이벤트 핸들러는 이벤트 메세지를 생성하는 EventMessageCreator에 이벤트를 생성하는 유즈케이스를 람다 인자로 넣어 이벤트를 만들어요.

``` kotlin
    sealed class EventMessage<out T>(
        open val target: EventTarget,
        open val txId: String,
    )
    
    data class TargetEventMessage<T>(
        override val target: EventTarget,
        override val txId: String,
        val message: T,
    ) : EventMessage<T>(target = target, txId = txId)
    
    data class ErrorEventMessage<T>(
        override val target: EventTarget,
        override val txId: String,
        val errorMessage: String,
    ) : EventMessage<T>(target = target, txId = txId)

```
- 이벤트는 TargetEventMessage(비즈니스적인 목표를 수행하는 메세지), ErrorEventMessage(비즈니스 로직 구현 중 발생한 에러)로 구성되요.

``` kotlin
    fun createMessage(eventTarget: EventTarget, txId: String, eventAction: () -> Event): EventMessage<Event> {
        return try {
            val event = eventAction.invoke()

            TargetEventMessage(
                target = eventTarget,
                txId = event.txId,
                message = event,
            )
        } catch (e: Exception) {
            ErrorEventMessage(
                target = eventTarget,
                txId = txId,
                errorMessage = e.message.toString()
            )
        }
    }
```
- 이벤트를 만드는 람다 인자를 받아 invoke()로 호출함으로써 위에 정의한 이벤트 메세지 형태로 구성해요.

``` kotlin
    override fun publish(eventName: EventPublishName, message: EventMessage<Event>) {
        kafkaTemplate.send(
            eventName.topicName,
            message.toString(),
        )
    }
```
- EventPublisher로 이벤트를 발행해요.

``` kotlin
    override suspend fun reject(txId: String, rejectReason: String) {
        val orderKitchenStatusEvent = updateRejectKitchenStatusEvent(txId)

        eventPublisher.publish(
            eventName = EventPublishName.ORDER_TO_KITCHEN_STATUS,
            message = orderKitchenStatusEvent,
        )
```
- 만약 정상 응답을 받았을 떄, 받은 이벤트가 REJECT 상태를 의미하거나, 에러 응답을 받은 경우 이전 단계에 대한 보상 트랜잭션을 진행해요

## 4. 문제점

- A 이벤트의 결과를 바탕으로 B 이벤트를 호출하는 결과에서 이벤트간 강한 결합이 발생해요.
  - 결합도를 줄이는 방법을 고민 중이에요!

## 5. 실행 (확정 아닙니다!)

```
docker compose up --build -d

docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic order-to-test
docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic order-to-product-product-stock
docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic order-to-customer-validation
docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic order-to-kitchen-status

docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic customer-to-order-customer-validation
docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic product-to-order-product-stock
docker exec -it [docker id] kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic kitchen-to-order-kitchen-status

docker exec -it [docker id] /usr/bin/kafka-topics --list --bootstrap-server localhost:9092
```

