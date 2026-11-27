package com.smartgiftcalendar.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.smartgiftcalendar.app.notifications.ReminderNotifier
import com.smartgiftcalendar.app.ui.SmartGiftApp
import com.smartgiftcalendar.app.ui.theme.SmartGiftCalendarTheme

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        ReminderNotifier.showDueReminders(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            ReminderNotifier.showDueReminders(this)
        }
        setContent {
            // Apply RTL layout direction across the whole app.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SmartGiftCalendarTheme {
                    SmartGiftApp()
                }
            }
        }
    }
}
