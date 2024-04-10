package com.raif.onlinecashier.models

import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Entity
@Table(name = "menus")
class MenuEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    val id: Int = 0,
    @Column(nullable = false)
    val chatId: Long = 0,
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val price: Double = 0.0,
) {
    constructor(chatId: Long, name: String, price: Double) : this(0, chatId, name, price)
}

@Repository
interface MenuEntityRepository : JpaRepository<MenuEntity, Int> {
    fun findByChatId(chatId: Long, page: Pageable): Page<MenuEntity>
    fun countByChatId(chatId: Long): Int
}