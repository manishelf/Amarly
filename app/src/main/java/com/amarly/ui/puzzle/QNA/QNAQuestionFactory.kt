package com.amarly.ui.puzzle.QNA

import android.content.Context
import com.amarly.data.Question
import com.amarly.repo.QuestionRepo

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

class QuestionFactory(private val context: Context) {

    val repo = QuestionRepo(context)

    fun getNextQuestion(): Question {
        /*
        return Question(
            text = q,
            options = o,
            answers = arrayOf(2, 3),
            reasoning = reasoning,
            difficulty = Difficulty.EASY
        )*/

        val index = repo.questionIndex

        val categoryKey = index.categories.keys.random()
        val category = index.categories[categoryKey]

        val topicKey = category!!.keys.random()
        val topic = category[topicKey]

        var difficulty = topic!!.keys.random()
        var questions = topic[difficulty]
        while (questions!!.isEmpty()) {
            difficulty = topic!!.keys.random()
            questions = topic[difficulty]
        }
        val questionUri = questions!!.random()

        val question = repo.loadSingleQuestion(questionUri)

        return question!!
    }
}