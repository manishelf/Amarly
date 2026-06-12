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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.amarly.ui.alarm.AlarmList
import com.amarly.ui.main.ActionButton
import com.amarly.ui.main.BottomBar
import com.amarly.ui.main.MainViewModel
import com.amarly.ui.main.SnackbarHost
import com.amarly.ui.main.TimePickerDialogue
import com.amarly.ui.main.TimePickerType
import com.amarly.ui.main.TopBar
import com.amarly.ui.theme.AmarlyTheme
import java.time.Instant

class MainActivity : ComponentActivity() {
    // what does by mean here?
    private val viewModel: MainViewModel by viewModels()
    val rootFolderPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->

            if (uri == null) return@registerForActivityResult

            try {
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                contentResolver.takePersistableUriPermission(uri, takeFlags)

                viewModel.setRootFolder(uri)

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()
        /*
        viewModel.addAlarm(
            AlarmData(
                1,
                ZonedDateTime.now().plusSeconds(10),
                puzzleType = PuzzleType.QNA,
                message = "Some sample text to test out the things"
            )
        )
         */

        setContent {
            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // TODO: this does not stop on toggle of alarm
                        val nextAlarmMillis by remember {
                            derivedStateOf {
                                val now = Instant.now()
                                viewModel.alarms
                                    .filter { it.running }
                                    .map { it.triggerInstant() }
                                    .filter { it.isAfter(now) }
                                    .minOrNull()?.toEpochMilli() ?: -1
                            }
                        }
                        TopBar(nextAlarmMillis, Modifier)
                    },
                    bottomBar = {
                        BottomBar()
                    },
                    snackbarHost = {
                        SnackbarHost() // like toast/conduit in bottom
                    },
                    floatingActionButton = {
                        ActionButton(
                            onAddOnceAlarm = {
                                viewModel.displayTimePicker = true
                                viewModel.timePickerMode = TimePickerType.QUICK
                            }, onAddRegularAlarm = {
                                viewModel.displayTimePicker = true
                                viewModel.timePickerMode = TimePickerType.REGULAR
                            })
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    //  containerColor= MaterialTheme.colorScheme.background,
                    //  contentColor= contentColorFor(containerColor),
                ) { innerPadding ->
                    AlarmList(
                        viewModel.alarms,
                        onToggle = {
                            viewModel.toggleAlarm(it)
                        },
                        onDelete = {
                            viewModel.deleteAlarm(it)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                    if (viewModel.displayTimePicker) {
                        TimePickerDialogue(
                            onConfirm = { alarmData ->
                                viewModel.addAlarm(alarmData)
                                viewModel.displayTimePicker = false
                            },
                            onDismiss = {
                                viewModel.displayTimePicker = false
                            },
                            type = viewModel.timePickerMode,
                            Modifier,
                        )
                    }
                }
            }
        }
    }

    fun checkPermissions() {
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
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU
            && !notificationManager.canUseFullScreenIntent()
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                .apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
        if (viewModel.getRootFolder() == null) {
            rootFolderPicker.launch(null)
        }
        // TODO: extra permissions for boot reciever and display over lock screen
    }
}

