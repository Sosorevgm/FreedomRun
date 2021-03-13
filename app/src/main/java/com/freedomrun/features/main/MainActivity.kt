package com.freedomrun.features.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.freedomrun.R

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_container, CurrentMaratonFragment())
            .commit()

    }

}