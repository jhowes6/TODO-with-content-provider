/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todowithcontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int TASK_EDIT = 1;
    public static final int TASK_ADD = -1;

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    Boolean viewingCompleted;

    /**
     * Initializes the database and floating-action-button
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewingCompleted = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // Create an adapter and supply the data to be displayed
        //updateTaskList(0);
        adapter = new TaskAdapter(this, viewingCompleted);
        // Connect the adapter with the recycler view
        recyclerView.setAdapter(adapter);
        // Give the recyclerView a default layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EditTaskActivity
                Intent intent = new Intent(getBaseContext(), EditTaskActivity.class);
                startActivityForResult(intent, TASK_EDIT);
            }
        });
    }

    /**
     * After an Edit-activity is called, this method inserts the new item or changes the selected
     * item in the database
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == TASK_EDIT && resultCode != Activity.RESULT_CANCELED){
            String task = data.getStringExtra(EditTaskActivity.EXTRA_TASK);
            int id = data.getIntExtra(EditTaskActivity.EXTRA_ID, -99);
            String date = data.getStringExtra(EditTaskActivity.EXTRA_DATE);
            int ic = data.getIntExtra(EditTaskActivity.EXTRA_ISCOMPLETE, 0);
            Log.d("MainActivity", "TASK: " + task);
            Log.d("MainActivity", "ID: " + id);
            Log.d("MainActivity", "DATE: " + date);
            Log.d("MainActivity", "is-complete: " + ic);


            if(task.length() != 0){
                ContentValues values = new ContentValues();
                values.put(Contract.TaskList.KEY_TASK, task);
                //values.put(Contract.TaskList.KEY_ID, id);
                values.put(Contract.TaskList.KEY_DATE, date);
                values.put(Contract.TaskList.KEY_ISCOMPLETE, ic);
                if(id == TASK_ADD){

                    getContentResolver().insert(Contract.CONTENT_URI, values);
                } else if(id >= 0){
                    String[] selectionArgs = {Integer.toString(id)};
                    getContentResolver().update(Contract.CONTENT_URI, values,
                            Contract.TaskList.KEY_ID, selectionArgs);
                }
                adapter.notifyDataSetChanged();
                recreate();
            }

        }
    }
    /**
     *
     * @param menu - a reference to the options menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(viewingCompleted){
            menu.findItem(R.id.action_view_completed).setTitle("View incomplete tasks");
        } else{
            menu.findItem(R.id.action_view_completed).setTitle("View completed tasks");
        }
        return true;
    }

    /**
     *
     * @param item - the menu item selected
     * @return true (if option-item selected is an actual option item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_view_completed) {
            if(!viewingCompleted){
                viewingCompleted = true;
                adapter = new TaskAdapter(this, viewingCompleted);
                // Connect the adapter with the recycler view
                recyclerView.setAdapter(adapter);
                // Give the recyclerView a default layout manager
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                item.setTitle("View incomplete tasks");

            } else{
                viewingCompleted = false;
                adapter = new TaskAdapter(this, viewingCompleted);
                // Connect the adapter with the recycler view
                recyclerView.setAdapter(adapter);
                // Give the recyclerView a default layout manager
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                item.setTitle("View complete Tasks");

            }
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
