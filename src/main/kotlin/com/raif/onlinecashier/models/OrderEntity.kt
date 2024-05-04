package com.raif.onlinecashier.models


import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val chatId: Long = 0,
    @ManyToOne()
    val menuItem: MenuEntity = MenuEntity(),
    @Min(value = 1, message = "amount must be greater than 0")
    var amount: Int = 0,
)

@Repository
interface OrderEntityRepository : JpaRepository<OrderEntity, Int> {
    fun findByChatId(chatId: Long, page: Pageable): Page<OrderEntity>
    fun countByChatId(chatId: Long): Int
    fun findByMenuItemId(orderId: Int): OrderEntity?
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderEntity o WHERE o.chatId = :chatId")
    fun deleteAllByChatId(chatId: Long)
}