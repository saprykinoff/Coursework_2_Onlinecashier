package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.MyInlineButton
import com.raif.onlinecashier.Utilities
import org.telegram.telegrambots.meta.api.objects.Update

class AddProductConfirmationState(
    private val stateController: StateController,
    private val name: String,
    private val price: Double
) : State {
    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id, params) = Utilities.parseCallback(query, "add_product_confirmation") ?: return this
            when (id) {
                "cancel" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController, 1)
                }

                "confirm" -> {
                    stateController.answer(query.id)
                    stateController.dataService.addMenuItem(stateController.chatId, name, price)
                    return MenuState(stateController, 1)
                }
            }
        }
        return this
    }

    override fun show() {
        val text =
            "Пожалуйста, проверьте информацию о товаре, который вы хотите добавить. \n" +
                    "Название: $name\n" +
                    "Цена: $price"
        val markup = Utilities.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Отмена", "cancel")),
                listOf(MyInlineButton("Добавить", "confirm"))
            ), "add_product_confirmation"
        )
        stateController.send(text, markup)
    }

}