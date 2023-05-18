package com.BAM.bam_projekt.ui.main

import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val userRepository = UserRepository()

    fun registerUser(username: String, password: String) {
        userRepository.registerUser(username, password)
    }
}
