package com.example.fieldsurfacearea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import yuku.ambilwarna.AmbilWarnaDialog

class AddField : AppCompatActivity() {
    companion object {
        var pickedColor = 0xFFFF0000.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_field)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val coloredBackground = findViewById<View>(R.id.backgroundColor)
        coloredBackground.setBackgroundColor(pickedColor)
        coloredBackground.setOnClickListener { openColorPicker() }

        val createFieldBtn = findViewById<Button>(R.id.createFieldBtn)
        createFieldBtn.setOnClickListener {
            val name = findViewById<TextView>(R.id.addFieldName)
            if (name.text.isBlank()) {
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, "Name can't be blank", duration)
                toast.show()
            } else {
                val field = createField()
                MainActivity.switchToCreateField(field)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun createField(): Field {
        val name = findViewById<TextView>(R.id.addFieldName)
//        val red = (pickedColor shr 16) and 0xff
//        val green = (pickedColor shr 8) and 0xff
//        val blue = pickedColor and 0xff
//        val rgbString = "$red,$green,$blue"
        return Field.build(name.text.toString(), pickedColor, arrayListOf())
    }

    private fun openColorPicker() {
        val ambilWarnaDialog = AmbilWarnaDialog(this, pickedColor, object: AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                pickedColor = color
                val coloredBackground = findViewById<View>(R.id.backgroundColor)
                coloredBackground.setBackgroundColor(pickedColor)
            }

            override fun onCancel(dialog: AmbilWarnaDialog?) {
                TODO("Not yet implemented")
            }
        })

        ambilWarnaDialog.show()
    }
}