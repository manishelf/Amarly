package com.amarly

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.amarly.data.AlarmData
import com.amarly.data.AlarmRepository
import com.amarly.service.AlarmScheduler
import com.amarly.ui.AlarmUi
import com.amarly.ui.theme.AmarlyTheme


class MainActivity : ComponentActivity() {
    val alarmRepo = AlarmRepository(this)
    val alarmScheduler = AlarmScheduler(this)

    val folderPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->

            if (uri == null) return@registerForActivityResult

            try {
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                contentResolver.takePersistableUriPermission(uri, takeFlags)

                alarmRepo.setStorageFolder(uri)

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if(alarmRepo.getStorageFolder() == null){
                folderPicker.launch(null)
            }
            val alarms = remember() {
                mutableStateListOf<AlarmData>().apply{
                    addAll(alarmScheduler.registerAll(alarmRepo.loadAll()))
                }
            }

            var displayTimePicker by remember() {
                mutableStateOf(false)
            }

            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AlarmUi.TopBar(alarms, Modifier)
                    },
                    floatingActionButton = {
                        AlarmUi.AddButton(
                            modifier = Modifier,
                            onClick = {
                                displayTimePicker = true
                            }
                        )
                    }
                ) { innerPadding ->
                    AlarmUi.AlarmList(
                        Modifier.padding(
                            innerPadding
                        ),
                        alarms,
                        deleteHandler = {
                            alarms.remove(it)
                            alarmRepo.deleteOne(it)
                            alarmScheduler.clear(it)
                        }
                    )

                    if (displayTimePicker) {
                        AlarmUi.TimePicker(
                            Modifier,
                            onConfirm = { alarmData ->
                                alarms.add(alarmData)
                                alarmRepo.saveOne(alarmData)
                                if(!alarmScheduler.register(alarmData)){
                                    this.startActivity(
                                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    )
                                }
                                displayTimePicker = false
                            },
                            onDismiss = {
                                displayTimePicker = false
                            }
                        )
                    }
                }
            }
        }
    }
}

