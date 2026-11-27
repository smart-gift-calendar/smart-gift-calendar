package com.smartgiftcalendar.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.Paparazzi
import com.smartgiftcalendar.app.data.AuthRepository
import com.smartgiftcalendar.app.data.UserAccount
import com.smartgiftcalendar.app.data.UserRole
import com.smartgiftcalendar.app.ui.screens.AdminScreen
import com.smartgiftcalendar.app.ui.screens.CalendarScreen
import com.smartgiftcalendar.app.ui.screens.CompanyScreen
import com.smartgiftcalendar.app.ui.screens.DashboardScreen
import com.smartgiftcalendar.app.ui.screens.InviteScreen
import com.smartgiftcalendar.app.ui.screens.LoginScreen
import com.smartgiftcalendar.app.ui.screens.ShopScreen
import com.smartgiftcalendar.app.ui.theme.SmartGiftCalendarTheme
import org.junit.Rule
import org.junit.Test

class ScreenshotTests {
    @get:Rule
    val paparazzi = Paparazzi()

    // Helper to render screens in Persian RTL.
    private fun snapshot(content: @androidx.compose.runtime.Composable () -> Unit) {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SmartGiftCalendarTheme {
                    content()
                }
            }
        }
    }

    @Test
    fun loginScreen() {
        snapshot { LoginScreen() }
    }

    @Test
    fun dashboardUser() {
        AuthRepository.currentAccount.value = UserAccount("user", "1234", "Alex User", UserRole.USER)
        snapshot {
            DashboardScreen({}, {}, {}, {}, {})
        }
    }

    @Test
    fun dashboardAdmin() {
        AuthRepository.currentAccount.value = UserAccount("admin", "admin123", "Sam Admin", UserRole.ADMIN)
        snapshot {
            DashboardScreen({}, {}, {}, {}, {})
        }
    }

    @Test
    fun dashboardCompany() {
        AuthRepository.currentAccount.value = UserAccount("company", "1234", "Casey Company", UserRole.COMPANY)
        snapshot {
            DashboardScreen({}, {}, {}, {}, {})
        }
    }

    @Test
    fun calendarScreen() {
        snapshot { CalendarScreen() }
    }

    @Test
    fun inviteScreen() {
        snapshot { InviteScreen() }
    }

    @Test
    fun shopScreen() {
        snapshot { ShopScreen() }
    }

    @Test
    fun adminScreen() {
        snapshot { AdminScreen() }
    }

    @Test
    fun companyScreen() {
        snapshot { CompanyScreen() }
    }
}
