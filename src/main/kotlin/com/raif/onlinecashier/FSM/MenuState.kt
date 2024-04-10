package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.Constants
import com.raif.onlinecashier.MyInlineButton
import com.raif.onlinecashier.Utilities
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.math.max
import kotlin.math.min

class MenuState(
    private val stateController: StateController,
    private var page: Int,
) : State {

    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id, params) = Utilities.parseCallback(query, "menu") ?: return this
            when (id) {
                "left" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController,page - 1)
                }

                "right" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController,  page + 1)
                }

                "add" -> {
                    stateController.answer(query.id)
                    return AddProductEnterNameState(stateController)
                }


                "delmode" -> {
                    stateController.answer(query.id)
                    return MenuDeletionModeState(stateController, page)
                }

                "exit" -> {
                    stateController.answer(query.id)
                    return HomeState(stateController)
                }

                "addToCart" -> {
                    //Add to order
                    val menuItemId = params[0].toString().toInt()
                    stateController.dataService.addOrderItem(stateController.chatId, menuItemId, 1)
                    val item = stateController.dataService.getMenuItem(menuItemId) ?: return this
                    stateController.answer(query.id, "Товар \"${item.name}\" успешно добавлен в корзину")
                    return this
                }
                "empty" -> {
                    stateController.answer(query.id)
                    return this
                }
            }

        }
        return this
    }


    override fun show() {
        val pageCount = stateController.dataService.getMenuPageCount(stateController.chatId)
        page = max(1, page)
        page = min(page, pageCount)


        val text =
            "Каталог товаров (<code>$page/$pageCount</code>) :\n" +
                    "Нажмите на товар, чтобы добавить его в корзину."
        val menu = stateController.dataService.getMenuPage(stateController.chatId, page - 1)
        val menuButtons = mutableListOf<List<MyInlineButton>>()
        for (ent in menu) {
            menuButtons.add(listOf(MyInlineButton("${ent.name} (${ent.price} руб)", "addToCart", listOf(ent.id, ent.name))))
        }
        for (i in menu.size..<Constants.ITEMS_ON_PAGE) {
            menuButtons.add(listOf(MyInlineButton()))
        }
        menuButtons.add(
            listOf(
                MyInlineButton(if (page > 1) "⬅\uFE0F" else " ", "left"),
                MyInlineButton("\uD83C\uDD95", "add"),
                MyInlineButton("\uD83D\uDDD1\uFE0F❌", "delmode"),
                MyInlineButton(if (page < pageCount) "➡\uFE0F" else " ", "right")
            )
        )
        menuButtons.add(listOf(MyInlineButton("Выход↩\uFE0F", "exit")))

        val markup = Utilities.makeInlineKeyboard(menuButtons, "menu")
        stateController.send(text, markup)


    }
}