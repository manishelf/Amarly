package com.amarly.data

typealias Questions = Map<Difficulty, List<String>>
typealias Topics = Map<String, Questions>
typealias Categories = Map<String, Topics>

class QuestionIndex(
    val version: Int = VERSION,
    var stale: Boolean = false,
    var categories: Categories,
) {
    companion object {
        val VERSION = 1
    }
}