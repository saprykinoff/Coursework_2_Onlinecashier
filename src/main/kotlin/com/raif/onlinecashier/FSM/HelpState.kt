package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.services.MyInlineButton
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update


class HelpState(
    private val stateController: StateController
) : State {

    private val logger = LoggerFactory.getLogger("DataLayer")

    override fun nextState(update: Update): State {
        if (update.hasCallbackQuery()) {
            val query = update.callbackQuery
            val (id) = stateController.parseCallback(query, "helppage") ?: return this
            when (id) {
                "back" -> {
                    stateController.answer(query.id)
                    return HomeState(stateController)
                }
            }
        }
        return HomeState(stateController)
    }


    override fun show(): Int {
        val text = "Этот бот является заменой кассогвого аппарата для малого бизнеса\n" +
                "\n" +
                "<b>Каталог</b>:\n" +
                "1) Тут представлены товары которые вы продаете. Для читаемости товары разбиты на страницы по 5 элементов.\n" +
                "2) Для перерехода между страницами используются боковые кнопки внизу (кнопки неактивны если следующей/предыдущей страницы нет).\n" +
                "3) Чтобы добавить товар в каталог используйте кнопку \"\uD83C\uDD95\" внизу, а затем введите название и стоимость товара.\n" +
                "4) При нажатии на товар из каталога в обычном режиме, он добавляется в корзину\n" +
                "5) При нажатии на \"\uD83D\uDDD1\uFE0F❌\" значок изменится на \"\uD83D\uDDD1\uFE0F✅\" а вы перейдете в режим удаления.\n" +
                "В этом режиме при нажатии на товар из каталога происходит его удаление из каталога. Нажмите на корзину повторно, чтобы вернуться в обычный режим.\n" +
                "\n" +
                "<b>Последний заказ</b>\n" +
                "1) Отображает Qr-code последнего заказа.\n" +
                "2) Используйте кнопку \"Обновить\" чтобы узнать актуальный статус заказа\n" +
                "\n" +
                "<b>Корзина</b>\n" +
                "1) В корзине отображаются товары которые вы сейчас выбрали.\n Нажатие на элемент в корзине удаляет его в количестве 1 штуки.\n" +
                "2) В корзине также присутсвует пагинация с переходами между страницами\n" +
                "3) Использование кнопки \"Купить\uD83D\uDED2\" создает QR-code. Покажите его покупателю для оплаты\n"




        val markup = stateController.makeInlineKeyboard(
            listOf(
                listOf(MyInlineButton("Назад↩\uFE0F", "back"))
            ), "helppage"
        )

        return stateController.updateState(text, markup)
    }
}