package com.example.linemsgcatch.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.linemsgcatch.data.MessageOutput

class MemberDatabaseHelper(context: Context): SQLiteOpenHelper(context, "myMsg.db", null, 1){

    companion object {
        private const val TABLE_NAME = "myMsgTable"
        private const val ID = "id"
        private const val NAME = "name"
        private const val CONTENT = "content"
        private const val TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists $TABLE_NAME ( $ID integer PRIMARY KEY autoincrement, $NAME text, $CONTENT text, $TIME text)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    //Write Data
    fun addData(name: String?, content: String?, time: String) {
        val values = ContentValues()
        values.put(NAME, name)
        values.put(CONTENT, content)
        values.put(TIME, time)
        writableDatabase.insert(TABLE_NAME, null, values)
    }


    //Read Data
    fun getData(): MutableList<MessageOutput> {
        val cursor = readableDatabase.query(
            TABLE_NAME, arrayOf(
                ID,
                NAME,
                CONTENT,
                TIME
            ),
            null, null, null, null, null)
        val members = mutableListOf<MessageOutput>()

        try {
            if(cursor.moveToFirst()){
                do {
//                    val id = cursor.getInt(cursor.getColumnIndex(ID))
                    val name = cursor.getString(cursor.getColumnIndex(NAME))
                    val content = cursor.getString(cursor.getColumnIndex(CONTENT))
                    val time = cursor.getString(cursor.getColumnIndex(TIME))
                    val item = MessageOutput(
                        name,
                        content,
                        null,
                        time
                    )
                    members.add(item)
                } while(cursor.moveToNext())

            }
        } catch (e:Exception) {

        } finally {
            if(cursor != null && !cursor.isClosed){
                cursor.close()
            }
        }

        Log.e(">>>","總共有 ${cursor.count} 筆資料")
        return members

    }

    /*
    data class SqlInputData(val id:Int?, val name: String?, val content: String?, val time: String)

    //Read Data
    fun getData(): ArrayList<SqlInputData> {
        val cursor = readableDatabase.query(TABLE_NAME, arrayOf(ID, NAME, CONTENT, TIME),
            null, null, null, null, null)
        val members = ArrayList<SqlInputData>()

        try {
            if(cursor.moveToFirst()){
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(ID))
                    val name = cursor.getString(cursor.getColumnIndex(NAME))
                    val content = cursor.getString(cursor.getColumnIndex(CONTENT))
                    val time = cursor.getString(cursor.getColumnIndex(TIME))
                    val item = SqlInputData(id, name, content, time)
                    members.add(item)
                } while(cursor.moveToNext())

            }
        } catch (e:Exception) {

        } finally {
            if(cursor != null && !cursor.isClosed){
                cursor.close()
            }
        }

        Log.e(">>>","總共有 ${cursor.count} 筆資料")
        return members

    }
    */
}