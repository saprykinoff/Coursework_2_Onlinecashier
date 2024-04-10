package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.MyInlineButton
import com.raif.onlinecashier.Utilities

import org.telegram.telegrambots.meta.api.objects.Update

class HomeState(
    private val stateController: StateController
) : State {
    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id) = Utilities.parseCallback(query, "homepage") ?: return this
            when (id) {
                "menu" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController, 1)
                }
                "cart" -> {
                    stateController.answer(query.id)
                    return CartState(stateController, 1)
                }
                "lastOrder" -> {
                    stateController.answer(query.id)
                    val page = stateController.dataService.getQrPage(stateController.chatId, 0)
                    var idQr = -1
                    if (page.isNotEmpty()) {
                        idQr = page[0].id
                    }
                    return OrderDetailsState(stateController, idQr)
                }
            }
        }
        return HomeState(stateController)
    }


    override fun show() {
        val text = "Навигация:"
        val markup = Utilities.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Меню", "menu")),
                listOf(MyInlineButton("Последний заказ", "lastOrder")),
                listOf(MyInlineButton("Корзина", "cart")),
            ), "homepage"
        )

        stateController.send(text, markup)
    }
}
