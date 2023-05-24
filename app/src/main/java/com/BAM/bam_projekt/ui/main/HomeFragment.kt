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
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileNotFoundException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var dataManager: DataManager

    lateinit var cardNumber: EditText
    lateinit var cardExpiryDate: EditText
    lateinit var cardCvv: EditText
    lateinit var addButton: Button
    lateinit var readButton: Button
    lateinit var editButton: Button
    lateinit var deleteButton: Button
    lateinit var exportButton: Button
    lateinit var importButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataManager()

        cardNumber = view.findViewById<EditText>(R.id.cardNumber)
        cardExpiryDate = view.findViewById<EditText>(R.id.cardExpiryDate)
        cardCvv = view.findViewById<EditText>(R.id.cardCvv)
        addButton = view.findViewById<Button>(R.id.addButton)
        readButton = view.findViewById<Button>(R.id.readButton)
        editButton = view.findViewById<Button>(R.id.editButton)
        deleteButton = view.findViewById<Button>(R.id.deleteButton)
        exportButton = view.findViewById<Button>(R.id.exportButton)
        importButton = view.findViewById<Button>(R.id.importButton)


        addButton.setOnClickListener {addCard() }
        readButton.setOnClickListener { showCard() }
        editButton.setOnClickListener { editCard() }
        deleteButton.setOnClickListener { deleteCard() }
        exportButton.setOnClickListener { exportCard() }
        importButton.setOnClickListener { importCard() }

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

    fun addCard() {
        val number = cardNumber.text.toString()
        val expiryDate = cardExpiryDate.text.toString()
        val cvv = cardCvv.text.toString()

        if (number.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
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
        val card = CreditCard(
            number = number,
            expiryDate = expiryDate,
            cvv = cvv
        )
        dataManager.saveCard(card)
    }

    fun showCard() {
        val card = dataManager.getCard()
        cardNumber.setText(card?.number)
        cardExpiryDate.setText(card?.expiryDate)
        cardCvv.setText(card?.cvv)
    }

    fun editCard() {
        addCard()
        showCard()
    }

    fun deleteCard() {
        dataManager.deleteCard()
        cardNumber.setText(null)
        cardExpiryDate.setText(null)
        cardCvv.setText(null)
        if(dataManager.equals(null))
            Toast.makeText(requireContext(), "Karta usunięta", Toast.LENGTH_LONG).show()
    }

    private fun exportCard() {
        val card = dataManager.getCard()

        card?.let {
            val secretKey = "mySuperSecretKey" // This should be a securely generated key
            val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            val encryptedData = cipher.doFinal(it.toCsv().toByteArray())
//            val encryptedData = it.toCsv().toByteArray()
            val filePath = requireContext().filesDir.absolutePath + "/exportedData.csv"
            File(filePath).writeBytes(encryptedData)
            Toast.makeText(requireContext(), "Dane zostały wyeksportowane do pliku exportedData.csv", Toast.LENGTH_LONG).show()
        } ?:
            Toast.makeText(requireContext(), "Brak zapisanych kart", Toast.LENGTH_LONG).show()
    }

    private fun importCard() {
        val secretKey = "mySuperSecretKey" // This should be a securely generated key
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        try{
            val filePath = requireContext().filesDir.absolutePath + "/exportedData.csv"
            val encryptedData = File(filePath).readBytes()
            val card = CreditCard.fromCsv(encryptedData, cipher)

            dataManager.saveCard(card)
            Toast.makeText(requireContext(), "Dane zostały zaimportowane z pliku exportedData.csv", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "Brak pliku exportedData.csv", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Wystąpił błąd podczas importu", Toast.LENGTH_SHORT).show()
        }
    }

}