package com.jerrwu.quintic.common.view

data class DayViewDataWrapper(
    var currentDay: Int,
    var currentMonth: Int,
    var currentYear: Int,
    var isShow: Boolean) {

    fun show() {
        isShow = true
    }

    fun hide() {
        isShow = false
    }

}