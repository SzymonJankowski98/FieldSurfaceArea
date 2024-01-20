package com.example.fieldsurfacearea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class FieldsList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fields_list)

        val backToMapBtn = findViewById<Button>(R.id.backToMap)
        backToMapBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val fieldsList = findViewById<ListView>(R.id.FieldsList)

        val point1 = Point(40.7128f, -74.0060f)
        val point2 = Point(34.0522f, -118.2437f)
        val point3 = Point(51.5074f, -0.1278f)

        val field1 = Field(1, arrayListOf(point1, point2, point3), "Red", "Field1")
        val field2 = Field(2, arrayListOf(point2, point3, point1), "Green", "Field2")

//        Field.list.add(field1);
//        Field.list.add(field2);

        val fieldsAdapter = FieldAdapter(this, Field.list)

        fieldsList.adapter = fieldsAdapter
    }
}