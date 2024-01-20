package com.example.fieldsurfacearea

import android.content.Context
import codewithcal.au.sqliteandroidstudiotutorial.SQLiteManager

class Field(var id: Int, val points: ArrayList<Point>, val color: String, val name: String) {
    companion object {
        var list = arrayListOf<Field>()

        fun delete(id: Int, context: Context) {
            SQLiteManager.instanceOfDatabase(context)?.deleteField(id)
            list.removeIf { field -> field.id == id }
        }

        fun build(name: String, color: String, points: ArrayList<Point>): Field {
            return Field(1, points, color, name)
        }
    }

    fun save(context: Context): Field {
        id = SQLiteManager.instanceOfDatabase(context)?.createField(name, color, points) ?: 1
        list.add(this)
        return Field(id, points, color, name)
    }
}
