package com.BAM.bam_projekt.ui.main

import android.os.Build
import javax.crypto.Cipher
import java.time.format.DateTimeFormatter


data class CreditCard(
    val number: String,
    val expiryDate: String,
    val cvv: String
) {
    init {
        require(number.length == 16) { "Numer karty musi mieć 16 cyfr" }
        require(number.all { it.isDigit() }) { "Numer karty musi składać się tylko z cyfr" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            require(DateTimeFormatter.ofPattern("MM/yy").parse(expiryDate) != null) { "Data wygaśnięcia musi być w formacie MM/yy" }
        }

        require(cvv.length == 3) { "CVV musi mieć 3 cyfry" }
        require(cvv.all { it.isDigit() }) { "CVV musi składać się tylko z cyfr" }
    }

    override fun toString(): String {
        val maskedNumber = number.mapIndexed { index, char ->
            if (index < number.length - 4) '*' else char
        }.joinToString("")
        return """
            Numer karty: $maskedNumber
            Data wygaśnięcia: $expiryDate
            CVV: ***
        """.trimIndent()
    }
    fun reveal(): String {
        return """
            Numer karty: $number
            Data wygaśnięcia: $expiryDate
            CVV: $cvv
        """.trimIndent()
    }

    fun toCsv(): String {
        val delimiter = ","
        return "$number$delimiter$expiryDate$delimiter$cvv"
    }

    fun encrypt(cipher: Cipher): ByteArray {
        return cipher.doFinal(toCsv().toByteArray())
    }

    companion object {
        fun fromCsv(data: ByteArray, cipher: Cipher): CreditCard {
            val decryptedData = String(cipher.doFinal(data))
            val parts = decryptedData.split(",")
            return CreditCard(parts[0], parts[1], parts[2])
        }
    }
}
