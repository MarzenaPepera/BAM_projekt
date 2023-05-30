package com.BAM.bam_projekt

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class CardInfoProvider : ContentProvider() {

    private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)) {
        addURI("com.BAM.bam_projekt", "card_info", 1)
        this
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(): Boolean {
        val masterKey = MasterKey.Builder(context!!)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context!!,
            "encrypted_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            1 -> {
                val cardNumber = sharedPreferences.getString("card_number", "")
                val cardExpiry = sharedPreferences.getString("card_expiry", "")
                val cardCVV = sharedPreferences.getString("card_cvv", "")
                val cursor = MatrixCursor(arrayOf("card_number", "card_expiry", "card_cvv"))
                cursor.addRow(arrayOf(cardNumber, cardExpiry, cardCVV))
                cursor
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }
}
