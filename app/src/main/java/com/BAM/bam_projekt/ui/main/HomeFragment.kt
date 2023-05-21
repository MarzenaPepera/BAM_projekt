package com.BAM.bam_projekt.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.BAM.bam_projekt.R
import com.BAM.bam_projekt.MainActivity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var creditCardManager: CreditCardManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCreditCardManager()

        val cardNumber = view.findViewById<EditText>(R.id.cardNumber)
        val cardExpiryDate = view.findViewById<EditText>(R.id.cardExpiryDate)
        val cardCvv = view.findViewById<EditText>(R.id.cardCvv)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val readButton = view.findViewById<Button>(R.id.readButton)


        addButton.setOnClickListener {
            val number = cardNumber.text.toString()
            val expiryDate = cardExpiryDate.text.toString()
            val cvv = cardCvv.text.toString()

            if (number.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
                addCard(number, expiryDate, cvv)
                Toast.makeText(
                    context, "Dodanie karty powiodło się.",
                    Toast.LENGTH_SHORT
                ).show()
            } else
                Snackbar.make(
                    requireView(),
                    "Please fill in all the fields.",
                    Snackbar.LENGTH_SHORT
                ).show()
        }

        readButton.setOnClickListener { onReadButtonClick() }

    }

    private fun onReadButtonClick() {
        Toast.makeText(requireContext(),"Card: ${creditCardManager.getCard()}",Toast.LENGTH_LONG).show()
    }

    private fun initCreditCardManager() {

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

        creditCardManager = CreditCardManager(sharedPreferences)

    }

    fun addCard(number: String, expiryDate: String, cvv: String) {
        val card = CreditCard(
            number = number,
            expiryDate = expiryDate,
            cvv = cvv
        )
        creditCardManager.saveCard(card)
    }
}