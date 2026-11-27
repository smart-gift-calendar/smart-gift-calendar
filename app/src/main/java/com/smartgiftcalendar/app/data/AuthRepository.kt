package com.smartgiftcalendar.app.data

import androidx.compose.runtime.mutableStateOf
import com.smartgiftcalendar.app.R

// Simple roles for fake local authentication.
enum class UserRole(val labelRes: Int) {
    USER(R.string.role_user),
    ADMIN(R.string.role_admin),
    COMPANY(R.string.role_company)
}

// Fake account used only for local demo login.
data class UserAccount(
    val username: String,
    val password: String,
    val displayName: String,
    val role: UserRole
)

object AuthRepository {
    // Local fake accounts (no Firebase).
    private val accounts = listOf(
        UserAccount("user", "1234", "Alex User", UserRole.USER),
        UserAccount("admin", "admin123", "Sam Admin", UserRole.ADMIN),
        UserAccount("company", "1234", "Casey Company", UserRole.COMPANY)
    )

    val currentAccount = mutableStateOf<UserAccount?>(null)

    fun login(username: String, password: String): Boolean {
        val account = accounts.firstOrNull {
            it.username == username.trim() && it.password == password
        }
        currentAccount.value = account
        if (account != null) {
            // Keep existing demo data personalized after login.
            FakeRepository.currentUser.value = FakeRepository.currentUser.value.copy(name = account.displayName)
        }
        return account != null
    }

    fun logout() {
        currentAccount.value = null
    }

    fun canAccessAdmin(): Boolean = currentAccount.value?.role == UserRole.ADMIN

    fun canAccessCompany(): Boolean = currentAccount.value?.role == UserRole.COMPANY
}
