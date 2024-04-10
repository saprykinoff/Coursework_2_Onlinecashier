package com.raif.onlinecashier.models

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(nullable = false)
    val chatId: Long = 0,

    @ManyToOne(cascade = [])
    val menuItem: MenuEntity = MenuEntity(),
    @Column(nullable = false)
    @Min(value = 1, message = "amount must be greater than 0")
    var amount: Int = 0,
) {
    constructor(chatId: Long, menuItem: MenuEntity, amount: Int) : this(0, chatId, menuItem, amount)
}

@Repository
interface OrderEntityRepository : JpaRepository<OrderEntity, Int> {
    fun findByChatId(chatId: Long, page: Pageable): Page<OrderEntity>
    fun countByChatId(chatId: Long): Int
    fun findByMenuItemId(orderId: Int): OrderEntity?
    fun deleteByChatId(chatId: Long)
}