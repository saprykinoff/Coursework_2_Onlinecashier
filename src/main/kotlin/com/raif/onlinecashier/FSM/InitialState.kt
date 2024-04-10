package com.raif.onlinecashier.FSM

import org.telegram.telegrambots.meta.api.objects.Update

class InitialState(
    private val stateController: StateController
) : State {
    override fun nextState(update: Update): State {
        return HomeState(stateController)
    }

    override fun show() {

    }
}