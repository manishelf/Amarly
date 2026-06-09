package com.amarly.repo

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import com.amarly.data.AlarmData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule


class FileRepo(private val app: Application) {
    companion object {
        private const val PREFS = "almarly_prefs"
        private const val KEY_FOLDER_URI = "almarly_root_folder_uri"
    }

    val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .registerModule(JavaTimeModule())

    fun setRootFolder(uri: Uri?) {
        app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_FOLDER_URI, uri.toString())
            }
    }

    fun getRootFolder(): Uri? {
        val uriString = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_FOLDER_URI, null)
        return uriString?.let(Uri::parse)
    }

    fun saveOne(alarm: AlarmData): Boolean {
        val root = getRootFolder() ?: return false
        val folder = DocumentFile.fromTreeUri(app, root) ?: return false
        val file = folder.createFile(
            "application/xyaml",
            alarm.id() + ".yaml"
        ) ?: return false
        app.contentResolver.openOutputStream(
            file.uri,
            "wt"
        )?.use { stream ->
            mapper.writeValue(stream, alarm)
        }
        return true
    }

    fun getAllAlarms(): List<AlarmData> {
        val root = getRootFolder() ?: return emptyList()
        val folder = DocumentFile.fromTreeUri(app, root) ?: return emptyList()
        return folder.listFiles()
            .filter {
                (it.name?.endsWith(".yaml") == true) || (it.name?.endsWith(".yml") == true)
            }
            .mapNotNull { file ->
                try {
                    app.contentResolver
                        .openInputStream(file.uri)
                        ?.use { stream ->
                            mapper.readValue(
                                stream,
                                AlarmData::class.java
                            )
                        }
                } catch (e: Exception) {
                    Log.e("Amarly", e.message, e)
                    null
                }
            }
    }

    fun deleteOneAlarm(alarm: AlarmData): Boolean {
        val rootUri = getRootFolder() ?: return false
        val root = DocumentFile.fromTreeUri(app, rootUri) ?: return false
        val fileName = "${alarm.id()}.yaml"
        val file = root.listFiles().find { it.name == fileName } ?: return false
        return file.delete()
    }

    fun getById(id: String): AlarmData? {
        val rootUri = getRootFolder() ?: return null
        val root = DocumentFile.fromTreeUri(app, rootUri) ?: return null
        val fileName = "$id.yaml"
        val file = root.listFiles().firstOrNull { it.name == fileName }
            ?: return null

        return try {
            app.contentResolver.openInputStream(file.uri)?.use { stream ->
                mapper.readValue(stream, AlarmData::class.java)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun deleteAllAlarm(): Boolean {
        val root = getRootFolder() ?: return false
        val folder = DocumentFile.fromTreeUri(app, root) ?: return false
        val result = folder.delete()
        if (result) {
            setRootFolder(null)
        }
        return result
    }
}