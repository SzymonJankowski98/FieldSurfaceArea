package com.example.fieldsurfacearea

import android.content.Context
import codewithcal.au.sqliteandroidstudiotutorial.SQLiteManager
import com.mobile.areacounter.geometry.GeometryHelper
import com.mobile.areacounter.geometry.Polygon
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
        val pointsOffsetOne = listOf<Point>(*points.toTypedArray(), points[0])
            .subList(1, points.size + 1)

        val vectors = points.zip(pointsOffsetOne)
            .map { GeometryHelper.toMatPoint(it.first, it.second) }.toList()

        val aggregatedVectorList = vectors.map {sumPreviousVector(vectors.subList(0, vectors.indexOf(it) + 1))}

        return GeometryHelper.calculateMonteCarloArea(Polygon(aggregatedVectorList))
    }

    private fun sumPreviousVector(partialVectors: List<Vector>): Vector {
        val res =  partialVectors.reduce { acc, vector ->  acc.add(vector)}
        return res
    }
}
