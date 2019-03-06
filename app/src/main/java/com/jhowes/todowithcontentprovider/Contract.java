package com.jhowes.todowithcontentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String DATABASE_NAME = "tasklist";
    public static final int COMPLETE_TASKS = -2;
    public static final int INCOMPLETE_TASKS = -3;
    public static final String COUNT = "count";
    public static final String AUTHORITY =
            "com.jhowes.todowithcontentprovider.provider";
    public static final String CONTENT_PATH = "words";
    public static final String CONTENT_COMPLETE_TASKS_PATH = "complete_words";

    // URI's
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
    public static final Uri CONTENT_COMPLETE_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_COMPLETE_TASKS_PATH);
    public static final Uri ROW_COUNT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH + "/" + COUNT);

    // MIME types
    static final String SINGLE_RECORD_MIME_TYPE =
            "vnd.android.cursor.item/vnd.com.example.provider.words";
    static final String MULTIPLE_RECORDS_MIME_TYPE =
            "vnd.android.cursor.item/vnd.com.example.provider.words";


    /**
     * (empty constructor)
     */
    private Contract(){}

    /**
     * In inner-class which represents the table.
     *
     * (Implementing BaseColumns helps this class to work with cursor adapters)
     */
    public static abstract class TaskList implements BaseColumns {
        public static final String TASK_TABLE = "tasks";

        // Column names
        public static final String KEY_ID = "_id";
        public static final String KEY_TASK = "task";
        public static final String KEY_ISCOMPLETE = "is_complete";
        public static final String KEY_DATE = "date";
    }
}
