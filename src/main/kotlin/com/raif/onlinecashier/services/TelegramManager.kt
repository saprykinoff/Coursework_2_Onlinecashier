package com.raif.onlinecashier.services

import com.raif.onlinecashier.FSM.HomeState
import com.raif.onlinecashier.FSM.InitialState
import com.raif.onlinecashier.FSM.State
import com.raif.onlinecashier.FSM.StateController
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class TelegramManager(
    private val telegramService: TelegramService,
    private val dataService: DataService,
    private val utilityService: UtilityService,
) {

    private var states: MutableMap<Long, State> = mutableMapOf()


    @EventListener
    fun update(update: Update) {
        val chatId: Long
        val stateController: StateController
        if (update.hasMessage()) {
            chatId = update.message.chatId
            stateController = StateController(telegramService, dataService, utilityService, chatId)
        } else if (update.hasCallbackQuery()) {
            chatId = update.callbackQuery.message.chatId
            stateController = StateController(telegramService, dataService, utilityService, chatId)
        } else {
            return
        }

        try {
            states[chatId] = states.getOrDefault(chatId, InitialState(stateController)).nextState(update)
        } catch (e: Exception) {
            states[chatId] = HomeState(stateController)
        }
        states[chatId]!!.show()
    }


}
