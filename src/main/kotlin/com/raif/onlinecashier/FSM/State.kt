package com.raif.onlinecashier.FSM

import org.telegram.telegrambots.meta.api.objects.Update

interface State {
    fun nextState(update: Update): State
    fun show() : Int
}
