package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.Constants
import com.raif.onlinecashier.MyInlineButton
import com.raif.onlinecashier.Utilities
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.math.max
import kotlin.math.min

class CartState(
    private val stateController: StateController,
    private var page: Int,
) : State {

    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id, params) = Utilities.parseCallback(query, "cart") ?: return this
            when (id) {
                "left" -> {
                    stateController.answer(query.id)
                    return CartState(stateController, page - 1)
                }

                "right" -> {
                    stateController.answer(query.id)
                    return CartState(stateController, page + 1)
                }

                "deleteAll" -> {
                    stateController.dataService.clearCart(stateController.chatId)
                    stateController.answer(query.id)
                    return CartState(stateController, 1)
                }

                "exit" -> {
                    stateController.answer(query.id)
                    return HomeState(stateController)
                }

                "delete" -> {
                    //Add to order
                    val menuId = params[0].toString().toInt()
                    println(menuId)
                    stateController.dataService.delOrderItem(menuId)
                    val item = stateController.dataService.getMenuItem(menuId) ?: return this
                    stateController.answer(query.id, "Товар \"${item.name}\" успешно удален из корзины")
                    return this
                }
                "buy" -> {
                    val amount = stateController.dataService.calcOrderPrice(stateController.chatId)
                    val qr = stateController.dataService.createQr(amount, stateController.chatId)
                    stateController.answer(query.id)
                    return OrderDetailsState(stateController, qr?.id ?: -1)
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
        val pageCount = stateController.dataService.getOrderPageCount(stateController.chatId)
        page = max(1, page)
        page = min(page, pageCount)


        val text =
            "Ваша корзина (<code>$page/$pageCount</code>) :\n" +
                    "Нажмите на товар, чтобы удалить его из корзины."
        val menu = stateController.dataService.getOrderPage(stateController.chatId, page - 1)

        val menuButtons = mutableListOf<List<MyInlineButton>>()
        for (ent in menu) {
            menuButtons.add(
                listOf(
                    MyInlineButton(
                        "${ent.menuItem.name} (${ent.amount}x${ent.menuItem.price} = ${ent.menuItem.price * ent.amount} руб)",
                        "delete",
                        listOf(ent.menuItem.id) //TODO можно заменить на просто ent.id
                    )
                )
            )
        }
        for (i in menu.size..<Constants.ITEMS_ON_PAGE) {
            menuButtons.add(listOf(MyInlineButton()))
        }
        menuButtons.add(
            listOf(
                MyInlineButton(if (page > 1) "⬅\uFE0F" else " ", "left"),
                MyInlineButton("\uD83D\uDDD1\uFE0F", "deleteAll"),
                MyInlineButton(if (page < pageCount) "➡\uFE0F" else " ", "right")
            )
        )
        val price = stateController.dataService.calcOrderPrice(stateController.chatId)
        menuButtons.add(listOf(MyInlineButton("Купить\uD83D\uDED2($price Руб.)", "buy")))
        menuButtons.add(listOf(MyInlineButton("Выход↩\uFE0F", "exit")))

        val markup = Utilities.makeInlineKeyboard(menuButtons, "cart")
        stateController.send(text, markup)


    }
}