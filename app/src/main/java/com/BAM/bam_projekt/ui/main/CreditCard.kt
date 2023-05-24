package com.BAM.bam_projekt.ui.main

import javax.crypto.Cipher

data class CreditCard(
    val number: String,
    val expiryDate: String,
    val cvv: String
) {
    fun toCsv(): String {
        val delimiter = ","
        return "$number$delimiter$expiryDate$delimiter$cvv"
    }

    fun encrypt(cipher: Cipher): ByteArray {
        return cipher.doFinal(toCsv().toByteArray())
    }
}
