package com.BAM.bam_projekt.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.BAM.bam_projekt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dataManager: DataManager

    lateinit var loginButton: Button
    lateinit var button_to_register: Button
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initDataManager()
        checkIfUserLoggedIn()

        loginButton = view.findViewById<Button>(R.id.button_login)
        editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        button_to_register = view.findViewById<Button>(R.id.button_to_register)

        loginButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            login(email, password)
        }

        button_to_register.setOnClickListener {
            navToRegister()
        }
    }

    private fun initDataManager() {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "encrypted_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        dataManager = DataManager(sharedPreferences)
    }

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataManager.saveUserCredentials(email, password)
                    navToHome()
                } else {
                    Toast.makeText(context, "Brak autentykacji",Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Proszę uzupełnić wszystkie pola",Toast.LENGTH_SHORT).show()
        }
    }

    private fun navToRegister() {
        val navController = findNavController()
        navController.navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun navToHome() {
        val navController = findNavController()
        navController.navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun checkIfUserLoggedIn() {
        val user = dataManager.getUserCredentials()
        if (user.first != null && user.second != null) {
            login(user.first!!, user.second!!)
        }
    }
}
