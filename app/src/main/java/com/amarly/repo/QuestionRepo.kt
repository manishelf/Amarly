package com.amarly.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.amarly.data.Difficulty
import com.amarly.data.Question
import com.amarly.data.QuestionIndex
import com.amarly.data.Questions
import com.amarly.data.Topics
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class QuestionRepo(private val context: Context) {
    companion object {
        private const val PREFS = "almarly_prefs"
        private const val KEY_FOLDER = "amarly_root_folder_uri"
        private const val QNA_FOLDER = "qna"
        private const val QNA_INDEX = "qna_index"
    }

    val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    private var index: QuestionIndex? = null

    val questionIndex: QuestionIndex
        get() {
            if (index == null) {
                index = buildIndex()
                return index!!
            } else {
                return index!!
            }
        }

    fun getRootFolder(): Uri {
        val uriString = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_FOLDER, null)
        return uriString!!.toUri()
    }

    private fun findDocument(path: String): DocumentFile? {
        val root = DocumentFile.fromTreeUri(context, getRootFolder()) ?: return null
        var current = root.findFile(QNA_FOLDER) ?: return null
        path.trim('/')
            .split('/')
            .filter { it.isNotBlank() }
            // this is cool
            .forEach { segment ->
                current = current.findFile(segment) ?: return null
            }

        return current
    }

    fun buildIndex(): QuestionIndex {

        val emptyIndex = QuestionIndex(
            stale = true,
            categories = emptyMap()
        )

        val indexDoc = findDocument("$QNA_INDEX.yaml")
        if (indexDoc != null) {
            try {
                context.contentResolver.openInputStream(indexDoc.uri)?.use { stream ->
                    val index = mapper.readValue(stream, QuestionIndex::class.java)
                    if (!index.stale) {
                        return index
                    }
                }
            } catch (e: Exception) {
                Log.e("Amarly", "Error deserializing index", e)
            }
        }

        val root = DocumentFile.fromTreeUri(context, getRootFolder())
            ?: return emptyIndex
        val qnaFolder = root.findFile(QNA_FOLDER) ?: return emptyIndex

        val categories = mutableMapOf<String, Topics>()

        qnaFolder.listFiles()
            .filter { it.isDirectory }
            .forEach { categoryDir ->

                val topics = mutableMapOf<String, Questions>()

                categoryDir.listFiles()
                    .filter { it.isDirectory }
                    .forEach { topicDir ->

                        val questionsByDifficulty =
                            mutableMapOf<Difficulty, MutableList<String>>()

                        Difficulty.entries.forEach {
                            questionsByDifficulty[it] = mutableListOf()
                        }

                        topicDir.listFiles()
                            .filter {
                                it.isFile &&
                                        it.name?.endsWith(".yaml", ignoreCase = true) == true
                            }
                            .forEach { questionFile ->

                                try {
                                    context.contentResolver
                                        .openInputStream(questionFile.uri)
                                        ?.use { stream ->

                                            val question = mapper.readValue(
                                                stream,
                                                Question::class.java
                                            )

                                            questionsByDifficulty
                                                .getOrPut(question.difficulty) {
                                                    mutableListOf()
                                                }
                                                .add(questionFile.uri.toString())
                                        }
                                } catch (e: Exception) {
                                    Log.e(
                                        "Amarly",
                                        "Failed to index ${questionFile.name}",
                                        e
                                    )
                                }
                            }

                        topics[topicDir.name ?: "Unknown"] =
                            questionsByDifficulty.mapValues { (_, paths) ->
                                paths.toList()
                            }
                    }

                categories[categoryDir.name ?: "Unknown"] = topics
            }

        val index = QuestionIndex(
            stale = false,
            categories = categories
        )
        val file = qnaFolder.createFile(
            "application/xyaml",
            "$QNA_INDEX.yaml"
        ) ?: return index
        context.contentResolver.openOutputStream(
            file.uri,
            "w"
        )?.use { stream ->
            mapper.writeValue(stream, index)
        } ?: false

        return index
    }


    fun loadSingleQuestion(location: String): Question? {
        return try {
            val stream = if (location.startsWith("content://")) {
                context.contentResolver.openInputStream(location.toUri())
            } else {
                findDocument(location)?.let {
                    context.contentResolver.openInputStream(it.uri)
                }
            }

            stream?.use {
                mapper.readValue(it, Question::class.java)
            }
        } catch (e: Exception) {
            Log.e("Amarly", "Error deserializing question", e)
            null
        }
    }

    fun loadQuestionsInFolder(path: String): Array<Question> {
        val folder = findDocument(path) ?: return emptyArray()
        val questions = mutableListOf<Question>()
        folder.listFiles()
            .filter { it.isFile }
            .forEach { file ->
                try {
                    context.contentResolver.openInputStream(file.uri)?.use { stream ->
                        questions.add(
                            mapper.readValue(
                                stream,
                                Question::class.java
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Amarly", "Error deserializing question", e)
                }
            }
        return questions.toTypedArray()
    }

    fun loadAllQuestionsInCategory(category: String): Array<Question> {
        return loadQuestionsInFolder(category)
    }

    fun loadAllQuestionsInTopic(
        category: String,
        topic: String
    ): Array<Question> {
        return loadQuestionsInFolder("$category/$topic")
    }

    fun saveQuestion(category: String, topic: String, question: Question): Boolean {
        val folder = findDocument("$category/$topic")
            ?: return false

        val fileName =
            question.text.take(20)
                .replace(' ', '_') + ".yaml"

        val existing = folder.findFile(fileName)
        existing?.delete()

        val file = folder.createFile(
            "application/xyaml",
            fileName
        ) ?: return false

        context.contentResolver.openOutputStream(
            file.uri,
            "w"
        )?.use { stream ->
            mapper.writeValue(stream, question)
        } ?: false

        return true
    }
}