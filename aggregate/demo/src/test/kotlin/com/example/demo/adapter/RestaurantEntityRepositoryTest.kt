package com.example.demo.adapter

import com.example.demo.adapter.entity.RestaurantEntity
import com.example.demo.adapter.persistance.RestaurantEntityRepository
import com.example.demo.domain.Address
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class RestaurantEntityRepositoryTest(
    @Autowired private val restaurantEntityRepository: RestaurantEntityRepository,
) : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    given("주소와 레스토랑이 주어져요") {

        val address = Address("seoul")

        val restaurantEntity = RestaurantEntity(
            name = "test",
            address = address,
            orderId = 1L,
        )

        `when`("엔티티를 저장하고 그 값을 리턴하면") {

            val savedRestaurantEntity = restaurantEntityRepository.save(restaurantEntity)

            then("Address 값은 동등하되, 참조한 객체만 동일해야 해요") {
                savedRestaurantEntity.address shouldBe address
                savedRestaurantEntity.address shouldBe Address("seoul")
                savedRestaurantEntity.address shouldBeSameInstanceAs address
                savedRestaurantEntity.address shouldNotBeSameInstanceAs Address("seoul")
            }

            then("RestaurantEntity entity 동일 객체여야 해요") {
                restaurantEntity shouldBeSameInstanceAs  savedRestaurantEntity
                restaurantEntity.id shouldNotBe 0L
            }

            then("생성된 시간을 파악해요") {
                println("[CreatedAt Time By Entity]       =    ${savedRestaurantEntity.createdAt}")

                println("[CreatedAt Time By FindEntity]   =    " +
                        "${restaurantEntityRepository
                            .findById(savedRestaurantEntity.id)
                            .orElseThrow()
                            .createdAt}")
            }
        }

        `when`("리플렉션으로 id를 수정하면") {
            val id = RestaurantEntity::class.java.getDeclaredField("id")
            id.isAccessible = true
            id.set(restaurantEntity, 100L)

            then("식별자의 id는 100L이 되어야 해요") {
                restaurantEntity.id shouldBe 100L
            }
        }
    }
})