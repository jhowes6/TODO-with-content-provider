package com.jhowes.todowithcontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static java.lang.Integer.parseInt;

public class MyContentProvider extends ContentProvider {
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper DB;
    private static final int URI_INCOMPLETE_ITEMS_CODE = 10;
    private static final int URI_COMPLETE_ITEMS_CODE = 20;
    private static final int URI_ONE_ITEM_CODE = 30;
    private static final int URI_COUNT_CODE = 40;


    @Override
    public boolean onCreate() {
        DB = new DatabaseHelper(getContext());
        initializeUriMatching();
        return true;
    }
    private void initializeUriMatching(){
        matcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH, URI_INCOMPLETE_ITEMS_CODE);
        matcher.addURI(Contract.AUTHORITY, Contract.CONTENT_COMPLETE_TASKS_PATH, URI_COMPLETE_ITEMS_CODE);
        matcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
        matcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/" + Contract.COUNT, URI_COUNT_CODE );
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        switch(matcher.match(uri)){
            case URI_INCOMPLETE_ITEMS_CODE:
                cursor = DB.query(Contract.INCOMPLETE_TASKS);
                break;
            case URI_COMPLETE_ITEMS_CODE:
                cursor = DB.query(Contract.COMPLETE_TASKS);
                break;
            case URI_ONE_ITEM_CODE:
                cursor = DB.query(parseInt(uri.getLastPathSegment()));
                break;
            case URI_COUNT_CODE:
                cursor = DB.count();
                break;
            case UriMatcher.NO_MATCH:
                Log.d("ContentProvider", "No match for this URI: " + uri);
                break;
            default:
                Log.d("ContentProvider", "INVALID URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case URI_INCOMPLETE_ITEMS_CODE:
                return Contract.MULTIPLE_RECORDS_MIME_TYPE;
            case URI_ONE_ITEM_CODE:
                return Contract.SINGLE_RECORD_MIME_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = DB.insert(values);
        return Uri.parse(Contract.CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = parseInt(selectionArgs[0]);
        int ic = values.getAsInteger(Contract.TaskList.KEY_ISCOMPLETE);
        String task = values.getAsString(Contract.TaskList.KEY_TASK);
        return DB.updateTask(id, ic, task);
    }
}
