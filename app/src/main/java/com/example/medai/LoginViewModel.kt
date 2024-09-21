package com.example.medai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _userProfile.value = UserProfile(username = username, email = "$username@example.com")
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        _userProfile.value = null
        _isLoggedIn.value = false
    }
}
data class UserProfile(val username: String, val email: String)