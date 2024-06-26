package com.raif.onlinecashier.models

import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Entity
@Table(name = "qrs")
class QrObject(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val qrId: String = "",
    val payload: String = "",
    val qrUrl: String = "",
    var qrStatus: String = "",
    val chatId: Long = 0,
    val amount: Double = 0.0,
)

@Repository
interface QrObjectRepository : JpaRepository<QrObject, Int> {
    fun findAllByQrStatus(qrStatus: String): List<QrObject>
    fun findAllByChatId(chatId: Long, pageable: Pageable): Page<QrObject>
}