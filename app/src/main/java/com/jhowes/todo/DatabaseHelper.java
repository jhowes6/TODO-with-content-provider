/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String TASK_TABLE = "tasks";
    private static final String DATABASE_NAME = "tasklist";
    private static final int DATABASE_VERSION = 1;

    // Column names
    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_ISCOMPLETE = "is_complete";
    public static final String KEY_DATE = "date";

    // String array of columns
    private static final String[] COLUMNS = { KEY_ID, KEY_TASK, KEY_ISCOMPLETE, KEY_DATE };

    // SQL query that creates the table
    private static final String TASK_TABLE_CREATE =
            "CREATE TABLE " + TASK_TABLE + " ( " + KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_TASK + " TEXT, " + KEY_ISCOMPLETE + " INTEGER, " +
                    KEY_DATE + " TEXT);";

    // References to the database
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Creates the empty database
     * Calls fillWithData()
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASK_TABLE_CREATE);
    }
    /**
     * inserts a new item into the database
     *
     * @param task
     * @param date
     * @param isComplete
     * @return
     */
    public long insert(String task, String date, int isComplete){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_TASK, task);
        values.put(KEY_DATE, date);
        values.put(KEY_ISCOMPLETE, isComplete);
        try{
            if(writableDatabase == null) writableDatabase = getWritableDatabase();
            // Insert the row
            newId = writableDatabase.insert(TASK_TABLE, null, values);
        } catch(Exception e){
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        } finally{
            return newId;
        }
    }

    /**
     * Updates the value of 'isComplete' and 'task' for a specific item in the database
     *
     * @param id
     * @param ic
     * @param t
     * @return
     */
    public int updateTask(int id, int ic, String t){
        int numRowsUpdated = -1;
        try{
            if(writableDatabase == null) writableDatabase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_TASK, t);
            values.put(KEY_ISCOMPLETE, ic);
            numRowsUpdated = writableDatabase.update(TASK_TABLE, values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch(Exception e){
            e.printStackTrace();
        }
        return numRowsUpdated;
    }

    /**
     * Returns the TaskItem located at 'position' in the database
     * @param position
     * @return
     */
    public TaskItem query(int position){

        // construct a query that returns only the nth row of a result
        String query = "SELECT * FROM " + TASK_TABLE + " ORDER BY " +
                KEY_TASK + " ASC " + "LIMIT " + position + ",1";
        Cursor cursor = null;
        TaskItem entry = new TaskItem();
        try{
            if (readableDatabase == null) readableDatabase = getReadableDatabase();
            cursor = readableDatabase.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            entry.setTask(cursor.getString(cursor.getColumnIndex(KEY_TASK)));
            entry.setComplete(cursor.getInt(cursor.getColumnIndex(KEY_ISCOMPLETE)));
            entry.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            cursor.close();
            return entry;
        }
    }

    /**
     * Returns a list of the incomplete tasks from the database
     * @return
     */
    public Cursor getIncompleteTasks(){

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + KEY_ISCOMPLETE
                + " = 0 " + " ORDER BY " +
                KEY_TASK + " ASC ";
        Cursor cursor = null;
        try{
            if(readableDatabase == null) readableDatabase = getReadableDatabase();
            cursor = readableDatabase.rawQuery(query, null);
        } catch(Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * Returns a list of the complete tasks from the database
     * @return
     */
    public Cursor getCompleteTasks(){
        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + KEY_ISCOMPLETE
                + " = 1 " + " ORDER BY " +
                KEY_TASK + " ASC ";
        Cursor cursor = null;
        try{
            if(readableDatabase == null) readableDatabase = getReadableDatabase();
            cursor = readableDatabase.rawQuery(query, null);
        }catch(Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * Returns the number of incomplete tasks
     * @return
     */
    public long count(){
        if(readableDatabase == null) readableDatabase = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(readableDatabase, TASK_TABLE);
    }
    /**
     * Called when the database version is upgraded
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " +
                        newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }
}
