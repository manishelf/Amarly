package com.amarly.ui.puzzle.QNA

class Question(
    var text: String = "",
    var options: Array<String> = arrayOf(),
    var answer: Int = -1,
    var reasoning: String = ""
) {}

val q = "What is the radius of the moon or some generic question that is too long or too short"
val o = arrayOf(
    "10_000 some long ass option in case  KM",
    "10_000 some long ass option in case  KM",
    "10_000 some long ass option in case  KM",
    "10_000 some long ass option in case  KM",
)
val a = 2

val reasoning =
    "Some scientific reasoning yada yada yada \n or maybe some facts or something \n that can be verified or learned from"


fun getNextQuestion(): Question {
    return Question(
        q,
        o,
        a,
        reasoning
    )
}