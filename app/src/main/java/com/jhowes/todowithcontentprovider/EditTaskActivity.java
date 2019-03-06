/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todowithcontentprovider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "com.jhowes.todo.TASK";
    public static final String EXTRA_DATE = "com.jhowes.todo.DATE";
    public static final String EXTRA_ISCOMPLETE = "com.jhowes.todo.ISCOMPLETE";
    public static final String EXTRA_ID = "com.jhowes.todo.ID";


    private EditText taskEditText;
    private TextView dateLabel, dateTextView;
    private CheckBox checkBox;
    private Button saveButton;
    private String date, task;
    int isComplete;
    int mID = MainActivity.TASK_ADD;

    /**
     * Initializes the edit activity
     * Inflates the edit activity with data from the intent extras
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        taskEditText = (EditText) findViewById(R.id.edit_task);
        dateLabel = (TextView) findViewById(R.id.date_label);
        dateTextView = (TextView) findViewById(R.id.date_textview);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        saveButton = (Button) findViewById(R.id.save_button);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dateLabel.setText(R.string.date_completed);
                    Date rDate = Calendar.getInstance().getTime();
                    date = rDate.toString();
                    dateTextView.setText(date);
                }
            }
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            int id = extras.getInt(TaskAdapter.EXTRA_ID, -99);
            task = extras.getString(TaskAdapter.EXTRA_TASK, "");
            date = extras.getString(TaskAdapter.EXTRA_DATE, "");
            isComplete = extras.getInt(TaskAdapter.EXTRA_ISCOMPLETE, 0);
            if(id != -99 && task != ""){
                mID = id;
                taskEditText.setText(task);
                dateTextView.setText(date);
                if(isComplete == 1){
                    checkBox.setChecked(true);
                    dateLabel.setText(R.string.date_completed);
                } else checkBox.setChecked(false);
            }
        } else{
            Date rDate = Calendar.getInstance().getTime();
            isComplete = 0;
            checkBox.setChecked(false);
            date = rDate.toString();
            dateTextView.setText(date);
        }
    }

    /**
     * Returns a reply which includes the new TaskItem info
     *
     * @param v - the save button
     */
    public void returnReply(View v){
        task = taskEditText.getText().toString();
        if(checkBox.isChecked()){
            isComplete = 1;
        } else isComplete = 0;
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_ID,mID);
        replyIntent.putExtra(EXTRA_TASK, task);
        replyIntent.putExtra(EXTRA_DATE, date);
        replyIntent.putExtra(EXTRA_ISCOMPLETE, isComplete);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
