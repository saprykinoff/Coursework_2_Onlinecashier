package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.MyInlineButton
import com.raif.onlinecashier.Utilities
import org.telegram.telegrambots.meta.api.objects.Update

class OrderDetailsState (
    private val stateController: StateController,
    private val idQr: Int,
) : State {
    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id) = Utilities.parseCallback(query, "orderDetails") ?: return this
            when (id) {
                "home" -> {
                    stateController.answer(query.id)
                    return HomeState(stateController)
                }
                "update" -> {
                    stateController.answer(query.id)
                    return this
                }
            }
        }
        return this
    }

    override fun show() {
        val qrObject = stateController.dataService.getQrObject(idQr)
        val text = if (qrObject == null) {
            "Ошибка при загрузке. Попробуйте позже"
        } else {
            "Заказ <code>${qrObject.qrId}</code>:\n" +
                    "Сумма: ${qrObject.amount} Руб.\n" +
                    "Статус: ${qrObject.qrStatus}"
        }


        val markup = Utilities.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Обновить", "update")),
                listOf(MyInlineButton("Назад", "home"))
            ), "orderDetails"
        )
        if (qrObject == null) {
            stateController.send(text, markup)
        } else {
            stateController.sendPhoto(qrObject.qrUrl, text, markup)
        }
    }

}
