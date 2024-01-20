package com.example.fieldsurfacearea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class FieldsList : AppCompatActivity() {
    companion object {
        lateinit var fieldsAdapter: FieldAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fields_list)

        val backToMapBtn = findViewById<Button>(R.id.backToMap)
        backToMapBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val fieldsList = findViewById<ListView>(R.id.FieldsList)

        fieldsAdapter = FieldAdapter(this, Field.list)

        fieldsList.adapter = fieldsAdapter
    }
}