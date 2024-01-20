package com.example.fieldsurfacearea

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class FieldAdapter(context: Context, fields: List<Field>) : ArrayAdapter<Field>(context, 0, fields) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.field_cell, parent, false)
        val field = getItem(position) ?: return view

        val fieldName = view.findViewById<TextView>(R.id.fieldName)
        val fieldColor = view.findViewById<TextView>(R.id.fieldColor)

        fieldName.text = field.name
        fieldColor.text = field.color

        val fieldsListBtn = view.findViewById<Button>(R.id.deleteFieldBtn)
        fieldsListBtn.setOnClickListener { Field.delete(field.id, context) }

        return view
    }
}