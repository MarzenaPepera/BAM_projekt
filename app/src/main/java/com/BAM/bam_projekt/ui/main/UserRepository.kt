package com.BAM.bam_projekt.ui.main

class UserRepository {

    private val databaseService = DatabaseService()

    fun registerUser(username: String, password: String) {
        val user = User(username, password)
        databaseService.insertUser(user)
    }
}

