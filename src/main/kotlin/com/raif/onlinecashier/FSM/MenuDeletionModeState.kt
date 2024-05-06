package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.Constants
import com.raif.onlinecashier.services.MyInlineButton
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.math.max
import kotlin.math.min

class MenuDeletionModeState(
    private val stateController: StateController,
    private var page: Int,
) : State {
    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id, params) = stateController.parseCallback(query, "del_menu") ?: return this
            when (id) {
                "left" -> {
                    stateController.answer(query.id)
                    return MenuDeletionModeState(stateController, page - 1)
                }

                "right" -> {
                    stateController.answer(query.id)
                    return MenuDeletionModeState(stateController, page + 1)
                }

                "add" -> {
                    stateController.answer(query.id)
                    return AddProductEnterNameState(stateController)
                }

                "delmode" -> {
                    stateController.answer(query.id)
                    return MenuState(stateController, page)
                }

                "exit" -> {
                    stateController.answer(query.id)
                    return HomeState(stateController)
                }

                "deleteFromMenu" -> {
                    //Add to order
                    val menuItemId = params[0].toString().toInt()
                    val item = stateController.dataService.getMenuItem(menuItemId) ?: return this
                    stateController.answer(query.id, "Товар \"${item.name}\" успешно удален")
                    stateController.dataService.delMenuItem(menuItemId)
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


    override fun show(): Int {
        val pageCount = stateController.dataService.getMenuPageCount(stateController.chatId)
        page = max(1, page)
        page = min(page, pageCount)
        val text =
            "Каталог товаров (<code>$page/$pageCount</code>) :\n" +
                    "Нажмите на товар, чтобы <b><i><u>УДАЛИТЬ</u></i></b> его из списка товаров."
        val menu = stateController.dataService.getMenuPage(stateController.chatId, page - 1)
        val menuButtons = mutableListOf<List<MyInlineButton>>()
        for (ent in menu) {
            menuButtons.add(
                listOf(MyInlineButton("${ent.name} (${ent.price} руб)", "deleteFromMenu", listOf(ent.id)))
            )
        }
        for (i in menu.size..<Constants.ITEMS_ON_PAGE) {
            menuButtons.add(listOf(MyInlineButton()))
        }
        menuButtons.add(
            listOf(
                MyInlineButton(if (page > 1) "⬅\uFE0F" else " ", "left"),
                MyInlineButton("\uD83C\uDD95", "add"),
                MyInlineButton("\uD83D\uDDD1\uFE0F✅", "delmode"),
                MyInlineButton(if (page < pageCount) "➡\uFE0F" else " ", "right")
            )
        )
        menuButtons.add(listOf(MyInlineButton("Назад↩\uFE0F", "exit")))

        val markup = stateController.makeInlineKeyboard(menuButtons, "del_menu")

        return stateController.updateState(text, markup)
    }
}