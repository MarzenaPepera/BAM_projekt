package com.BAM.bam_projekt.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.BAM.bam_projekt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    //private lateinit var viewModel: RegisterViewModel
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        //viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        // Przykładowe zdarzenie kliknięcia przycisku rejestracji
        val registerButton = view.findViewById<Button>(R.id.button_register)
        val emailInput = view.findViewById<EditText>(R.id.input_email)
        val passwordInput = view.findViewById<EditText>(R.id.input_password)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            Log.d("RegisterFragment", "Email: $email, Password: $password")

            //viewModel.registerUser(username, password)
            registerNewUser(email, password)

        }
    }
    private fun registerNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Rejestracja udana, aktualizuj interfejs użytkownika z danymi użytkownika
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val navController = findNavController()
                    navController.navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    // Jeśli rejestracja nie powiedzie się, wyświetl komunikat dla użytkownika.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Rejestracja nie powiodła się.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}
