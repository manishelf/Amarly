package com.amarly.ui.main

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amarly.data.AlarmData
import com.amarly.repo.AlarmScheduler
import com.amarly.repo.FileRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FileRepo(app)
    private val scheduler = AlarmScheduler(app)

    val alarms = mutableStateListOf<AlarmData>()

    var isLoaded by mutableStateOf(false)
        private set

    var displayTimePicker by mutableStateOf(false)

    var timePickerMode by mutableStateOf(TimePickerType.REGULAR)


    init {
        loadAlarms()
    }

    fun loadAlarms() {
        isLoaded = false
        // requires context since it is coroutine?
        // and requires priority?
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = scheduler.registerAll(repo.getAllAlarms())
            withContext(Dispatchers.Main) {
                alarms.addAll(loaded)
                isLoaded = true
            }
        }
    }

    fun addAlarm(alarm: AlarmData): Boolean {
        alarms.add(alarm)
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveOne(alarm)
        }
        return scheduler.register(alarm)
    }

    fun toggleAlarm(alarm: AlarmData) {
        alarm.running = !alarm.running
        updateAlarm(alarm)
    }

    fun deleteAlarm(alarm: AlarmData) {
        alarms.remove(alarm)
        scheduler.clear(alarm)
        repo.deleteOneAlarm(alarm)
    }

    fun updateAlarm(alarm: AlarmData): AlarmData {
        TODO("")
    }

    fun duplicateAlarm(alarm: AlarmData): AlarmData {
        TODO("")
    }

    fun getRootFolder(): Uri? {
        return repo.getRootFolder()
    }

    fun setRootFolder(uri: Uri) {
        repo.setRootFolder(uri)
    }

    fun nextAlarmMillis(): Long {
        return alarms
            .filter { it.running }
            .map { it.triggerInstant() }
            .minOrNull()?.toEpochMilli() ?: -1
    }

}