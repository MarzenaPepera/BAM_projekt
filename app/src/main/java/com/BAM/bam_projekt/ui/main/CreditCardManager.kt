package com.BAM.bam_projekt.ui.main

import android.content.SharedPreferences

class CreditCardManager(private val sharedPreferences: SharedPreferences) {

    fun saveCard(card: CreditCard) {
        with(sharedPreferences.edit()) {
            putString("card_number", card.number)
            putString("card_expiry_date", card.expiryDate)
            putString("card_cvv", card.cvv)
            commit()
        }
    }

    fun getCard(): CreditCard? {
        val number = sharedPreferences.getString("card_number", null)
        val expiryDate = sharedPreferences.getString("card_expiry_date", null)
        val cvv = sharedPreferences.getString("card_cvv", null)

        if (number != null && expiryDate != null && cvv != null) {
            return CreditCard(number, expiryDate, cvv)
        }

        return null
    }

    fun deleteCard() {
        with(sharedPreferences.edit()) {
            remove("card_number")
            remove("card_expiry_date")
            remove("card_cvv")
            commit()
        }
    }
}
