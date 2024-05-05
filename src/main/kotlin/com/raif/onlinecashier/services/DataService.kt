package com.raif.onlinecashier.services

import com.raif.onlinecashier.Constants
import com.raif.onlinecashier.models.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class DataService(
    private val qrObjectRepository: QrObjectRepository,
    private val menuEntityRepository: MenuEntityRepository,
    private val orderEntityRepository: OrderEntityRepository,
    private val chatInfoRepository: ChatInfoRepository,
    private val cashMachineRepository: CashMachineRepository
) {

    private val logger = LoggerFactory.getLogger("DataLayer")

    fun addMenuItem(chatId: Long, name: String, price: Double) {
        val entity = MenuEntity(0, chatId, name, price)
        menuEntityRepository.saveAndFlush(entity)
        logger.info("Add to [$chatId] menu item ($name, $price)")
    }

    fun delMenuItem(id: Int) {
        val orderEnt = orderEntityRepository.findByMenuItemId(id)
        if (orderEnt != null) {
            orderEntityRepository.deleteById(orderEnt.id)
        }
        menuEntityRepository.deleteById(id)
    }

    fun addOrderItem(chatId: Long, menuId: Int, count: Int) {
        var entity = orderEntityRepository.findByMenuItemId(menuId)
        if (entity == null) {
            if (count < 0) return
            entity = OrderEntity(0, chatId, MenuEntity(menuId), count)
            orderEntityRepository.saveAndFlush(entity)
            logger.info("Add to [$chatId] order item ($menuId, $count)")
            return
        }
        entity.amount += count
        if (entity.amount <= 0) {
            orderEntityRepository.deleteById(entity.id)
            logger.info("Delete [$chatId, $menuId] ")
        } else {

            orderEntityRepository.saveAndFlush(entity)
            logger.info("Update to [$chatId, $menuId] order ($count)")
        }
    }

    fun delOrderItem(menuId: Int) {
        addOrderItem(0, menuId, -1)
    }

    fun clearCart(chatId: Long) {
        println("delete $chatId")
        orderEntityRepository.deleteAllByChatId(chatId)
    }

    fun getMenuPageCount(chatId: Long): Int {
        val x = menuEntityRepository.countByChatId(chatId)
        val y = Constants.ITEMS_ON_PAGE
        return maxOf((x + y - 1) / y, 1)
    }

    fun getOrderPageCount(chatId: Long): Int {
        val x = orderEntityRepository.countByChatId(chatId)
        val y = Constants.ITEMS_ON_PAGE
        return maxOf((x + y - 1) / y, 1)
    }

    fun getMenuPage(chatId: Long, page: Int): List<MenuEntity> {
        val pageable = PageRequest.of(page, Constants.ITEMS_ON_PAGE, Sort.by("name").ascending())
        val pageResult = menuEntityRepository.findByChatId(chatId, pageable)
        return pageResult.content
    }

    fun getOrderPage(chatId: Long, page: Int): List<OrderEntity> {
        val pageable = PageRequest.of(page, Constants.ITEMS_ON_PAGE, Sort.by("menuItem.name").ascending())
        val pageResult = orderEntityRepository.findByChatId(chatId, pageable)
        return pageResult.content
    }

    fun getQrPage(chatId: Long, page: Int): List<QrObject> {
        val pageable = PageRequest.of(page, Constants.ITEMS_ON_PAGE, Sort.by("id").descending())
        val pageResult = qrObjectRepository.findAllByChatId(chatId, pageable)
        return pageResult.content
    }

    fun getMenuItem(id: Int): MenuEntity? {
        return menuEntityRepository.findById(id).getOrNull()
    }

    fun getOrderItem(id: Int): OrderEntity? {
        return orderEntityRepository.findById(id).getOrNull()
    }

    fun getOrderItemByMenuItemId(menuItemId: Int): OrderEntity? {
        return orderEntityRepository.findByMenuItemId(menuItemId)
    }

    fun getQrObject(id: Int): QrObject? {
        return qrObjectRepository.findById(id).getOrNull()
    }

    fun createQr(amount: Double, chatId: Long, expDate: String = "+15m"): QrObject? {
        val empty = QrObject()
        qrObjectRepository.saveAndFlush(empty)
        val response = khttp.post(
            "${Constants.PAYMENT_API_URL}/qrs/dynamic",
            json = mapOf(
                "amount" to amount,
                "order" to "${Constants.STATIC_QR_PREFIX}_${empty.id}",
                "qrExpirationDate" to expDate
            )
        )
        val data = try {
            response.jsonObject
        } catch (e: Exception) {
            return null
        }
        val qrObject = QrObject(
            empty.id,
            data["qrId"].toString(),
            data["payload"].toString(),
            data["qrUrl"].toString(),
            data["qrStatus"].toString(),
            chatId,
            amount
        )
        qrObjectRepository.saveAndFlush(qrObject)
        logger.info("Qr ${qrObject.qrId}(${qrObject.id}, ${qrObject.amount} руб.) created")
        return qrObject
    }

    fun calcOrderPrice(chatId: Long): Double {
        var curSum = 0.0
        var pageNum = 0
        while (true) {
            val page = getOrderPage(chatId, pageNum)
            if (page.isEmpty()) break
            for (order in page) {
                curSum += order.menuItem.price * order.amount
            }
            pageNum += 1
        }
        return curSum
    }

    fun getChatInfo(chatId: Long): ChatInfo? {
        return chatInfoRepository.findById(chatId).getOrNull()
    }

    fun createCashMachine(chatId: Long, name: String, link: String = ""): CashMachine {
        val cm = CashMachine(chatId = chatId, name = name, qrVarLink = link)
        cashMachineRepository.saveAndFlush(cm)
        return cm
    }

    fun saveChatInfo(chatInfo: ChatInfo) {
        chatInfoRepository.saveAndFlush(chatInfo)
    }
}