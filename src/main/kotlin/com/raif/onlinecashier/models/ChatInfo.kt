package com.raif.onlinecashier.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity
class ChatInfo(
    @Id
    val chatId: Long = 0,
    @ManyToOne
    var cashMachine: CashMachine = CashMachine(),
    var lastMessage: Int = 0,
)


@Repository
interface ChatInfoRepository : JpaRepository<ChatInfo, Long> {

}