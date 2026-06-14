package com.amarly.ui.puzzle.QNA

import android.content.Context
import com.amarly.data.Difficulty
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

class QNAQuestionFactory(
    private val context: Context,
) {

    val repo = QuestionRepo(context)

    val shuffleOptions = true

    fun getNextQuestion(questionDifficulty: Difficulty = Difficulty.MIX): Question {
        /*
        return Question(
            text = q,
            options = o,
            answers = arrayOf(2, 3),
            reasoning = reasoning,
            difficulty = Difficulty.EASY
        )*/

        val index = repo.questionIndex

        // this is cool
        val allowedDifficulties = if (questionDifficulty == Difficulty.MIX) {
            Difficulty.entries.filter { it != Difficulty.MIX }
        } else {
            listOf(questionDifficulty)
        }

        while (true) {
            val category = index.categories.values.random()
            val topic = category.values.random()

            val availableDifficulties = allowedDifficulties.filter {
                !(topic[it]?.isEmpty()!!) // ;{
            }

            if (availableDifficulties.isEmpty()) {
                continue
            }

            val difficulty = availableDifficulties.random()
            val questionUri = topic[difficulty]!!.random()

            val question = repo.loadSingleQuestion(questionUri)!!

            question.answers = question.answers.map { it - 1 }.toTypedArray() // stored as one based

            if (shuffleOptions) {
                val oaMap = mutableMapOf<String, Int>()
                for ((index, option) in question.options.withIndex()) {
                    oaMap[option] = index
                }
                question.options.shuffle()
                val newAnswers = mutableListOf<Int>()
                for ((newIndex, option) in question.options.withIndex()) {
                    val oldIndex = oaMap[option]
                    if (question.answers.contains(oldIndex)) {
                        newAnswers.add(newIndex)
                    }
                }
                question.answers = newAnswers.toTypedArray()
            }

            return question
        }
    }
}