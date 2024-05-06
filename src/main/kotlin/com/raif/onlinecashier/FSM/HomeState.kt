package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.services.MyInlineButton
import org.slf4j.LoggerFactory


import org.telegram.telegrambots.meta.api.objects.Update

class HomeState(
    private val stateController: StateController
) : State {

    private val logger = LoggerFactory.getLogger("DataLayer")

    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id) = stateController.parseCallback(query, "homepage") ?: return this
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

                "help" -> {
                    stateController.answer(query.id)
                    return HelpState(stateController)
                }
            }
        }
        return HomeState(stateController)
    }


    override fun show(): Int {
        val text = "Навигация:"
        val markup = stateController.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Каталог", "menu")),
                listOf(MyInlineButton("Последний заказ", "lastOrder")),
                listOf(MyInlineButton("Корзина", "cart")),
                listOf(MyInlineButton("Помощь", "help")),
            ), "homepage"
        )

        return stateController.updateState(text, markup)
    }
}
