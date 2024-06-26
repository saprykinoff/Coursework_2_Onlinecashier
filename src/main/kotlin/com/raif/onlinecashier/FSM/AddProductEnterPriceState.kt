package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.services.MyInlineButton
import org.telegram.telegrambots.meta.api.objects.Update

class AddProductEnterPriceState(
    private val stateController: StateController,
    private val name: String
) : State {
    override fun nextState(update: Update): State {
        if (update.hasMessage()) {
            val price = update.message.text.toDoubleOrNull()
            if (price == null || price < 0) {
                stateController.send("Введите положительное дробное число через точку (12.34)")
            } else {
                return AddProductConfirmationState(stateController, name, price)
            }
        }
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id, params) = stateController.parseCallback(query, "add_product_price") ?: return this
            when (id) {
                "cancel" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController, 1)
                }
            }

        }
        return AddProductEnterPriceState(stateController, name)
    }

    override fun show(): Int {
        val text =
            "Пожалуйста, введите информацию о товаре, который вы хотите добавить. \n" +
                    "Название: $name\n" +
                    "Цена: ?"
        val markup = stateController.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Отмена", "cancel"))
            ), "add_product_price"
        )
        return stateController.updateState(text, markup)
    }

}