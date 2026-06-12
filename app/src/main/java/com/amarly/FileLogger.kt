package com.amarly

import android.content.Context

object FLog {

    private const val FILE_NAME = "logs.txt"
    val isON = true

    enum class LoggerLevel {
        INFO,
        ERROR,
        DEBUG,
    }

    fun log(context: Context, level: LoggerLevel, message: String) {
        if (isON) {
            try {
                val time = java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())

                val line = "($time) [$level] $message\n"

                context.openFileOutput(FILE_NAME, Context.MODE_APPEND).use {
                    it.write(line.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun i(context: Context, message: String) {
        log(context, LoggerLevel.INFO, message)
    }

    fun e(context: Context, message: String) {
        log(context, LoggerLevel.ERROR, message)
    }

    fun d(context: Context, message: String) {
        log(context, LoggerLevel.DEBUG, message)
    }

    fun readLogs(context: Context): String {
        return try {
            context.openFileInput(FILE_NAME).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }

    fun clear(context: Context) {
        context.deleteFile(FILE_NAME)
    }
}