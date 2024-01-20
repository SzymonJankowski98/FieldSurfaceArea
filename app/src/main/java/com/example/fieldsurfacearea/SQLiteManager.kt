package codewithcal.au.sqliteandroidstudiotutorial

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import com.example.fieldsurfacearea.Field
import com.example.fieldsurfacearea.Point


class SQLiteManager(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sql: StringBuilder = StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME)
            .append("(")
            .append(ID_COL)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(NAME_COL)
            .append(" TEXT, ")
            .append(COLOR_COL)
            .append(" INTEGER)")
        sqLiteDatabase.execSQL(sql.toString())

        val sql2: StringBuilder = StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME2)
            .append("(")
            .append(ID_COL)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(FIELD_ID_COL)
            .append(" INTEGER, ")
            .append(LATITUDE_COL)
            .append(" DOUBLE, ")
            .append(LONGITUDE_COL)
            .append(" DOUBLE)")
        sqLiteDatabase.execSQL(sql2.toString())
    }
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun createField(name: String, color: Int, points: ArrayList<Point>): Int {
        val sqLiteDatabase = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NAME_COL, name)
        contentValues.put(COLOR_COL, color)

        val fieldId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues).toInt()
        points.forEach { point -> createPoint(fieldId, point) }

        return fieldId
    }

    fun createPoint(fieldId: Int, point: Point){
        val sqLiteDatabase = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(FIELD_ID_COL, fieldId)
        contentValues.put(LATITUDE_COL, point.latitude)
        contentValues.put(LONGITUDE_COL, point.longitude)

        sqLiteDatabase.insert(TABLE_NAME2, null, contentValues)
    }

    fun deleteField(id: Int) {
        val sqLiteDatabase = this.writableDatabase
        sqLiteDatabase.delete(TABLE_NAME, "id = $id", null)
    }

    fun populateFieldsList() {
        Field.list.clear()
        val sqLiteDatabase = this.readableDatabase
        sqLiteDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null).use { result ->
            if (result.count != 0) {
                while (result.moveToNext()) {
                    val id = result.getInt(0)
                    val name = result.getString(1)
                    val color = result.getInt(2)

                    val points = getPoints(id)
                    val field = Field(id, points, color, name)

                    Field.list.add(field)
                }
            }
        }
    }

    private fun getPoints(fieldId: Int): ArrayList<Point> {
        val points = ArrayList<Point>()
        val sqLiteDatabase = this.readableDatabase
        sqLiteDatabase.rawQuery("SELECT * FROM $TABLE_NAME2 WHERE field_id = $fieldId", null).use { result ->
            if (result.count != 0) {
                while (result.moveToNext()) {
                    val latitude = result.getDouble(2)
                    val longitude = result.getDouble(3)
                    val point = Point(latitude, longitude)

                    points.add(point)
                }
            }
        }

        return points
    }

    companion object {
        private var sqLiteManager: SQLiteManager? = null
        private const val DATABASE_NAME = "FieldSurfaceAreaDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "fields"
        private const val ID_COL = "id"
        private const val NAME_COL = "name"
        private const val COLOR_COL = "color"

        private const val TABLE_NAME2 = "points"
        private const val LATITUDE_COL = "latitude"
        private const val LONGITUDE_COL = "longitude"
        private const val FIELD_ID_COL = "field_id"

        fun instanceOfDatabase(context: Context?): SQLiteManager? {
            if (sqLiteManager == null) sqLiteManager = SQLiteManager(context)
            return sqLiteManager
        }
    }
}