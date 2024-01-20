package com.example.fieldsurfacearea

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

        val pickColorBtn = findViewById<Button>(R.id.pickColor)
        pickColorBtn.setOnClickListener { openColorPicker() }

        val createFieldBtn = findViewById<Button>(R.id.createFieldBtn)
        createFieldBtn.setOnClickListener {
            var field = createField()
            MainActivity.switchToCreateField(field)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createField(): Field {
        val name = findViewById<TextView>(R.id.addFieldName)
//        val red = (pickedColor shr 16) and 0xff
//        val green = (pickedColor shr 8) and 0xff
//        val blue = pickedColor and 0xff
//        val rgbString = "$red,$green,$blue"
        return Field.build(name.text.toString(), pickedColor.toString(), arrayListOf())
    }

    private fun openColorPicker() {
        val ambilWarnaDialog = AmbilWarnaDialog(this, pickedColor, object: AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                pickedColor = color
                val coloredBackground = findViewById<Button>(R.id.button3)
                coloredBackground.setBackgroundColor(pickedColor)
            }

            override fun onCancel(dialog: AmbilWarnaDialog?) {
                TODO("Not yet implemented")
            }
        })

        ambilWarnaDialog.show()
    }
}