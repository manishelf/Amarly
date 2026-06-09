package com.amarly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.amarly.ui.alarm.AlarmList
import com.amarly.ui.main.ActionButton
import com.amarly.ui.main.BottomBar
import com.amarly.ui.main.MainViewModel
import com.amarly.ui.main.SnackbarHost
import com.amarly.ui.main.TimePickerDialogue
import com.amarly.ui.main.TimePickerType
import com.amarly.ui.main.TopBar
import com.amarly.ui.theme.AmarlyTheme

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
        setContent {
            LaunchedEffect({}) {
                if (viewModel.getRootFolder() == null) rootFolderPicker.launch(null)
            }
            AmarlyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(viewModel.nextAlarmMillis(), Modifier)
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
}

