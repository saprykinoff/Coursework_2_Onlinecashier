package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.services.DataService
import com.raif.onlinecashier.services.MyInlineButton
import com.raif.onlinecashier.services.TelegramService
import com.raif.onlinecashier.services.UtilitiyService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

class StateController(
    val telegramService: TelegramService,
    val dataService: DataService,
    val utilitiyService: UtilitiyService,
    val chatId: Long,
) {
    fun send(text: String, replyMarkup: ReplyKeyboard? = null, markdown: Boolean = false, replyTo: Int? = null): Int {
        return telegramService.sendMessage(chatId, text, replyMarkup, markdown, replyTo)
    }

    fun sendPhoto(
        text: String,
        url: String,
        replyMarkup: ReplyKeyboard? = null,
        markdown: Boolean = false,
        replyTo: Int? = null
    ): Int {
        return telegramService.sendPhoto(chatId, text, url, replyMarkup, markdown, replyTo)
    }

    fun answer(id: String, text: String = "", alert: Boolean = false) {
        telegramService.answerCallback(id, text, alert)
    }

    fun makeInlineKeyboard(buttons: List<List<MyInlineButton>>, callbackPrefix: String): InlineKeyboardMarkup {
        return utilitiyService.makeInlineKeyboard(buttons, callbackPrefix)
    }

    fun parseCallback(query: CallbackQuery, prefix: String): Pair<String, List<Any>>? {
        return utilitiyService.parseCallback(query, prefix)
    }
}
