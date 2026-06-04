package com.amarly

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.amarly.data.AlarmData
import com.amarly.data.AlarmRepository
import com.amarly.service.AlarmScheduler
import com.amarly.ui.AlarmMainUi
import com.amarly.ui.AlarmReceiverUi
import com.amarly.ui.theme.AmarlyTheme


class MainActivity : ComponentActivity() {
    val alarmRepo = AlarmRepository(this)
    val alarmScheduler = AlarmScheduler(this)
    val alarmMainUi = AlarmMainUi(this)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: popups for fol-
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {}.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivity(intent)
        }
        val notificationManager = this.getSystemService(
            NotificationManager::class.java
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU
            && !notificationManager.canUseFullScreenIntent()
        ) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
            ).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            this.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )
        }
        if(alarmRepo.getStorageFolder() == null){
            folderPicker.launch(null)
        }
        enableEdgeToEdge()
        setContent {
            val alarms = remember() {
                mutableStateListOf<AlarmData>().apply{
                    addAll(alarmScheduler.registerAll(alarmRepo.loadAll()))
                }
            }

            var displayTimePicker by remember() {
                mutableStateOf(false)
            }
            val reciever = Intent(this, AlarmReceiverUi::class.java).apply{
               putExtra("timer_id", alarms.get(0).id())
            }
            if(false)
                startActivity(reciever)
            else
            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        alarmMainUi.TopBar(alarms, Modifier)
                    },
                    floatingActionButton = {
                        alarmMainUi.AddButton(
                            modifier = Modifier,
                            onClick = {
                                displayTimePicker = true
                            }
                        )
                    }
                ) { innerPadding ->
                    alarmMainUi.AlarmList(
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
                        alarmMainUi.TimePicker(
                            Modifier,
                            onConfirm = { alarmData ->
                                alarms.add(alarmData)
                                alarmRepo.saveOne(alarmData)
                                alarmScheduler.register(alarmData)
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

