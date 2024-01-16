package codewithcal.au.sqliteandroidstudiotutorial

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log


class SQLiteManager(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sql: StringBuilder
        sql = StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME)
            .append("(")
            .append(ID_COL)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(NAME_COL)
            .append(" TEXT, ")
            .append(COLOR_COL)
            .append(" TEXT)")
        sqLiteDatabase.execSQL(sql.toString())

        val sql2: StringBuilder
        sql2 = StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME2)
            .append("(")
            .append(ID_COL)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(FIELD_ID_COL)
            .append(" INTEGER, ")
            .append(LATITUDE_COL)
            .append(" FLOAT, ")
            .append(LONGITUDE_COL)
            .append(" FLOAT)")
        sqLiteDatabase.execSQL(sql2.toString())
    }

    fun getFields(): ArrayList<Int> {
        var array: ArrayList<Int> = ArrayList<Int>()
        val sqLiteDatabase = this.readableDatabase
        sqLiteDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null).use { result ->
            if (result.count != 0) {
                while (result.moveToNext()) {
                    val id = result.getInt(0)
                    val name = result.getString(1)
                    val color = result.getString(2)
                    array.add(id)
                }
            }
        }

        return array
    }

    fun createField() {
        Log.e("TAG", "createField")

        val sqLiteDatabase = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NAME_COL, "test name")
        contentValues.put(COLOR_COL, "#FFFFFF")

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

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