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
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = scheduler.registerAll(repo.getAllAlarms())
            withContext(Dispatchers.Main) {
                alarms.clear()
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
        updateAlarm(alarm.copy(running = !alarm.running))
    }

    fun deleteAlarm(alarm: AlarmData) {
        alarms.remove(alarm)
        viewModelScope.launch {
            scheduler.clear(alarm)
            repo.deleteOneAlarm(alarm)
        }
    }

    // ID is not changed
    fun updateAlarm(alarm: AlarmData) {
        val index = alarms.indexOfFirst { it.id() == alarm.id() }
        if (index != -1) {
            alarms[index] = alarm
        }
        viewModelScope.launch {
            repo.saveOne(alarm)
            scheduler.clear(alarm)
            if (alarm.running) {
                scheduler.register(alarm)
            }
        }
    }

    // ID is updated
    fun updateAlarm(alarmOld: AlarmData, alarmNew: AlarmData) {
        viewModelScope.launch {
            repo.deleteOneAlarm(alarmOld)
            repo.saveOne(alarmNew)
            val index = alarms.indexOf(alarmOld)
            if (index != -1) {
                alarms[index] = alarmNew
            }
        }
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

}