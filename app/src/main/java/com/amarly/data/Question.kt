package com.amarly.data

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    ADVANCE
}

data class Question(
    val version: Int = VERSION,
    var text: String = "",
    var options: Array<String> = arrayOf(),
    var answers: Array<Int> = arrayOf(),
    var reasoning: String = "",
    var difficulty: Difficulty,
) {
    companion object {
        val VERSION = 1
    }
}