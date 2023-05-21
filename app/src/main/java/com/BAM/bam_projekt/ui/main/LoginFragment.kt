package com.BAM.bam_projekt.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.BAM.bam_projekt.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        val loginButton = view.findViewById<Button>(R.id.button_login)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val button_to_register = view.findViewById<Button>(R.id.button_to_register)

        editTextEmail.text=null
        editTextPassword.text=null

        loginButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // TODO findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                        Toast.makeText(context, "Logowanie powiodło się.",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(requireView(), "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                Snackbar.make(requireView(), "Please fill in all the fields.", Snackbar.LENGTH_SHORT).show()
            }
        }

        button_to_register.setOnClickListener {
            navToRegister()
        }
    }
    private fun navToRegister() {
        val navController = findNavController()
        navController.navigate(R.id.action_loginFragment_to_registerFragment)
    }
}
