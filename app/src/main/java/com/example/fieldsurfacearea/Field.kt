package com.example.fieldsurfacearea

import android.content.Context
import codewithcal.au.sqliteandroidstudiotutorial.SQLiteManager
import com.mobile.areacounter.geometry.MatPoint
import com.mobile.areacounter.geometry.Vector

class Field(var id: Int, val points: ArrayList<Point>, val color: Int, val name: String) {
    companion object {
        var list = arrayListOf<Field>()

        fun delete(id: Int, context: Context) {
            SQLiteManager.instanceOfDatabase(context)?.deleteField(id)
            list.removeIf { field -> field.id == id }
        }

        fun build(name: String, color: Int, points: ArrayList<Point>): Field {
            return Field(1, points, color, name)
        }
    }

    fun save(context: Context): Field {
        id = SQLiteManager.instanceOfDatabase(context)?.createField(name, color, points) ?: 1
        list.add(this)
        return Field(id, points, color, name)
    }

    fun surfaceAre(): Double {
        val matPoints = points.map { MatPoint(it.latitude, it.longitude)}
        val matPointWithoutFirst = matPoints.subList(1, matPoints.size)

        val vectors = matPoints.zip(matPointWithoutFirst)
            .map { Vector(it.first, it.second) }
            .toMutableList()
            .add(Vector(matPoints.last(), matPoints.last()))

        return 123.0//Polygon(vectors).
    }
}
