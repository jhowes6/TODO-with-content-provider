/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todowithcontentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.jhowes.todowithcontentprovider.Contract.TaskList.KEY_ID;
import static com.jhowes.todowithcontentprovider.Contract.TaskList.KEY_TASK;
import static com.jhowes.todowithcontentprovider.Contract.TaskList.KEY_DATE;
import static com.jhowes.todowithcontentprovider.Contract.TaskList.KEY_ISCOMPLETE;
import static com.jhowes.todowithcontentprovider.Contract.TaskList.TASK_TABLE;
import static com.jhowes.todowithcontentprovider.Contract.DATABASE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

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
        ContentValues v = new ContentValues();
        v.put(KEY_TASK, "penis");
        db.insert(TASK_TABLE, null, v);
    }
    /**
     * inserts a new item into the database
     *
     * @param values - a ContentValues containing the TaskItem to be inserted
     * @return
     */
    public long insert(ContentValues values){
        long newId = 0;

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
     * Returns a cursor pointing to a list of either complete or incomplete tasks
     *
     * @param position
     * @return
     */
    public Cursor query(int position){
        String query;
        if(position == Contract.INCOMPLETE_TASKS){
            query = "SELECT * FROM " + TASK_TABLE + " WHERE " + KEY_ISCOMPLETE +
                    " = 0 " + " ORDER BY " + KEY_TASK + " ASC ";
        } else{
            query = "SELECT * FROM " + TASK_TABLE + " WHERE " + KEY_ISCOMPLETE +
                    " = 1 " + " ORDER BY " + KEY_TASK + " ASC ";
        }

        Cursor cursor = null;
        try{
            if (readableDatabase == null) readableDatabase = this.getReadableDatabase();
            cursor = readableDatabase.rawQuery(query, null);
            //cursor.moveToFirst();
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            return cursor;
        }
    }


    /**
     * Returns the number of incomplete tasks
     * @return
     */
    public Cursor count(){
        MatrixCursor cursor = new MatrixCursor(new String[]{Contract.CONTENT_PATH});
        try{
            if(readableDatabase == null) readableDatabase = getReadableDatabase();
            int count = (int) DatabaseUtils.queryNumEntries(readableDatabase, TASK_TABLE);
            cursor.addRow(new Object[]{count});
        } catch(Exception e){
            e.printStackTrace();
        }
        return cursor;
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
