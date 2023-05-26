package com.BAM.bam_projekt.ui.main

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.BAM.bam_projekt.R
import java.io.File
import java.io.FileNotFoundException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
    lateinit var exportFilename: EditText
    lateinit var cardInfo: TextView
    lateinit var addButton: Button
    lateinit var revealButton: Button
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
        exportFilename = view.findViewById<EditText>(R.id.exportFilename)
        cardInfo = view.findViewById<TextView>(R.id.cardInfo)
        addButton = view.findViewById<Button>(R.id.addButton)
        revealButton = view.findViewById<Button>(R.id.revealButton)
        editButton = view.findViewById<Button>(R.id.editButton)
        deleteButton = view.findViewById<Button>(R.id.deleteButton)
        exportButton = view.findViewById<Button>(R.id.exportButton)
        importButton = view.findViewById<Button>(R.id.importButton)


        addButton.setOnClickListener {addCard() }
        revealButton.setOnClickListener {
            if(revealButton.text == "Odkryj dane")
                revealCard()
            else if(revealButton.text == "Ukryj dane")
                showCard()
        }
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

    fun validateCardNumber(cardNumber: String): Boolean {
        return cardNumber.matches(Regex("^[0-9]{16}$"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateExpiryDate(expiryDate: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("MM/yy")
            val date = formatter.parse(expiryDate)
            date != null
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun validateCVV(cvv: String): Boolean {
        return cvv.matches(Regex("^[0-9]{3}$"))
    }

    fun saveCard(number: String, expiryDate: String, cvv: String){
        val card = CreditCard(
            number = number,
            expiryDate = expiryDate,
            cvv = cvv
        )
        dataManager.saveCard(card)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCard() {
        val number = cardNumber.text.toString()
        val expiryDate = cardExpiryDate.text.toString()
        val cvv = cardCvv.text.toString()

        if (number.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
            if (validateCardNumber(number) && validateExpiryDate(expiryDate) && validateCVV(cvv)) {
                saveCard(number, expiryDate, cvv)
                showCard()
                Toast.makeText(context, "Dodanie karty powiodło się.", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(context, "Niepoprawne dane karty", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(context, "Proszę uzupełnić wszystkie pola", Toast.LENGTH_SHORT).show()
    }

    fun showCard() {
        val card = dataManager.getCard()
        if(card == null)
            Toast.makeText(requireContext(), "Brak zapisanych kart", Toast.LENGTH_LONG).show()
        else {
            cardInfo.text = "Dane karty:\n" + card?.toString()
            revealButton.text = "Odkryj dane"
        }
    }

    fun revealCard() {
        val card = dataManager.getCard()
        if (card == null)
            Toast.makeText(requireContext(), "Brak zapisanych kart", Toast.LENGTH_LONG).show()
        else {
            cardInfo.text = "Dane karty:\n" + card?.reveal()
            revealButton.text = "Ukryj dane"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editCard() {
        addCard()
        showCard()
    }

    fun deleteCard() {
        dataManager.deleteCard()
        cardNumber.setText(null)
        cardExpiryDate.setText(null)
        cardCvv.setText(null)
        cardInfo.text = ""
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
            if(exportFilename.text.toString().isEmpty())
                exportFilename.setText("exportedData")
            val filePath = requireContext().filesDir.absolutePath + "/" + exportFilename.text.toString() + ".csv"
            File(filePath).writeBytes(encryptedData)
            Toast.makeText(requireContext(), "Dane zostały wyeksportowane do pliku " + exportFilename.text.toString() + ".csv", Toast.LENGTH_LONG).show()
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
            showCard()
            Toast.makeText(requireContext(), "Dane zostały zaimportowane z pliku exportedData.csv", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "Brak pliku exportedData.csv", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Wystąpił błąd podczas importu", Toast.LENGTH_SHORT).show()
        }
    }

}