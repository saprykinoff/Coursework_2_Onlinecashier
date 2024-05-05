package com.raif.onlinecashier.models


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Entity
class CashMachine (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var name: String = "Касса",
    val chatId: Long = 0,
    @OneToMany
    @JoinColumn(name = "cash_machine_id")
    val orders: List<OrderEntity> = listOf(),
    val qrVarLink: String = "",
    var status: String = "INACTIVE",
)

@Repository
interface CashMachineRepository : JpaRepository<CashMachine, Int> {

}