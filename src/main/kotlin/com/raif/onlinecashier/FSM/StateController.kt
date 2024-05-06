package com.raif.onlinecashier.FSM

import com.raif.onlinecashier.models.ChatInfo
import com.raif.onlinecashier.services.DataService
import com.raif.onlinecashier.services.MyInlineButton
import com.raif.onlinecashier.services.TelegramService
import com.raif.onlinecashier.services.UtilityService
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

class StateController(
    val telegramService: TelegramService,
    val dataService: DataService,
    val utilityService: UtilityService,
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
        return utilityService.makeInlineKeyboard(buttons, callbackPrefix)
    }

    fun parseCallback(query: CallbackQuery, prefix: String): Pair<String, List<Any>>? {
        return utilityService.parseCallback(query, prefix)
    }

    fun updateState(text: String, replyMarkup: InlineKeyboardMarkup, url: String? = null): Int {
//        return telegramService.sendMessage(chatId, text, replyMarkup)
        var chatInfo = dataService.getChatInfo(chatId)
        val newMsg = if (url == null) {
            telegramService.sendMessage(chatId, text, replyMarkup)
        } else {
            telegramService.sendPhoto(chatId, url, text, replyMarkup)
        }
        if (chatInfo == null) {
            val cm = dataService.createCashMachine(chatId, "Касса")
            chatInfo = ChatInfo(chatId, cm, newMsg)
        } else {
            try {
                telegramService.deleteMessage(chatId, chatInfo.lastMessage)
            } catch (_: Exception) {

            }
            chatInfo.lastMessage = newMsg
        }
        dataService.saveChatInfo(chatInfo)
        return newMsg
    }
}
