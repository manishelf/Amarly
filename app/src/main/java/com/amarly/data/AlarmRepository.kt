package com.amarly.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class AlarmRepository(
    val context: Context
){
    companion object {
        private const val PREFS = "alarm_prefs"
        private const val KEY_FOLDER_URI = "alarm_folder_uri"
    }
    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun setStorageFolder(uri: Uri?) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_FOLDER_URI, uri.toString())
            }
    }

    fun getStorageFolder(): Uri? {
        val uriString = context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_FOLDER_URI, null)
        return uriString?.let(Uri::parse)
    }

    fun saveOne(alarm: AlarmData): Boolean{
        val root = getStorageFolder() ?: return false
        val folder = DocumentFile.fromTreeUri(context, root) ?: return false
        val file =  folder.createFile(
            "application/x-yaml",
            alarm.id()+".yaml"
        ) ?: return false
        context.contentResolver.openOutputStream(
            file.uri,
            "wt"
        )?.use { stream ->
            mapper.writeValue(stream, alarm)
        }
        return true
    }

    fun loadAll() : List<AlarmData>{
        val root = getStorageFolder() ?: return emptyList()
        val folder = DocumentFile.fromTreeUri(context, root) ?: return emptyList()
        return folder.listFiles()
            .filter {
                (it.name?.endsWith(".yaml") == true) || (it.name?.endsWith(".yml") == true)
            }
            .mapNotNull { file ->
                try {
                    context.contentResolver
                        .openInputStream(file.uri)
                        ?.use { stream ->
                            val t = mapper.readValue(
                                stream,
                                AlarmData::class.java
                            )
                            t.triggerTime.timeZone = t.timeZone
                            t // return t
                        }
                } catch (e: Exception) {
                    Log.e("Amarly", e.message, e)
                    null
                }
            }
    }

    fun deleteOne(alarm: AlarmData): Boolean {
        val rootUri = getStorageFolder() ?: return false
        val root = DocumentFile.fromTreeUri(context, rootUri) ?: return false
        val fileName = "${alarm.id()}.yaml"
        val file = root.listFiles().find { it.name == fileName } ?: return false
        return file.delete()
    }

    fun getById(id: String): AlarmData? {
        val rootUri = getStorageFolder() ?: return null
        val root = DocumentFile.fromTreeUri(context, rootUri) ?: return null
        val fileName = "$id.yaml"
        val file = root.listFiles().firstOrNull { it.name == fileName }
            ?: return null

        return try {
            context.contentResolver.openInputStream(file.uri)?.use { stream ->
                mapper.readValue(stream, AlarmData::class.java)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun deleteAll(): Boolean{
        val root = getStorageFolder() ?: return false
        val folder = DocumentFile.fromTreeUri(context, root) ?: return false
        val result = folder.delete()
        if(result){
            setStorageFolder(null)
        }
        return result
    }
}