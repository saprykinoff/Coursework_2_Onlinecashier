package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.services.MyInlineButton
import org.telegram.telegrambots.meta.api.objects.Update

class OrderDetailsState(
    private val stateController: StateController,
    private val idQr: Int,
) : State {
    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id) = stateController.parseCallback(query, "orderDetails") ?: return this
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

    override fun show(): Int {
        val qrObject = stateController.dataService.getQrObject(idQr)
        val text = if (qrObject == null) {
            "Ошибка при загрузке. Попробуйте позже"
        } else {
            "Заказ <code>${qrObject.qrId}</code>:\n" +
                    "Сумма: ${qrObject.amount} Руб.\n" +
                    "Статус: ${qrObject.qrStatus}"
        }


        val markup = stateController.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Обновить", "update")),
                listOf(MyInlineButton("Назад↩\uFE0F", "home"))
            ), "orderDetails"
        )
        if (qrObject == null) {
            return stateController.updateState(text, markup)
        } else {
            return stateController.updateState(text, markup, qrObject.qrUrl)
        }
    }

}
