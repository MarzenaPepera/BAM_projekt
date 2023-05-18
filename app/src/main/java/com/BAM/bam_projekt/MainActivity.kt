package com.BAM.bam_projekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.BAM.bam_projekt.ui.main.MainFragment
import com.BAM.bam_projekt.ui.main.RegisterFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // This is the code that is executed when the app is started
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, MainFragment.newInstance())
//                .commitNow()
//        }

        // Tutaj możemy zainicjalizować nasz pierwszy fragment, np. RegisterFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegisterFragment())
            .commit()
    }
}