/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    DatabaseHelper DB;
    private Context mContext;
    private Cursor incompleteTasks;

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_TASK = "TASK";
    public static final String EXTRA_DATE = "DATE";
    public static final String EXTRA_POSITION = "POSITION";
    public static final String EXTRA_ISCOMPLETE = "IS_COMPLETE";
    public static final String EXTRA_FROMRECYCLER = "FROM_RECYCLER";

    /**
     * Constructor for TaskAdapter
     *
     *
     * @param context
     */
    public TaskAdapter(Context context, Cursor cursor) {
        mContext = context;
        incompleteTasks = cursor;
    }

     /**
     *   ViewHolder for TaskItems
     */
    class TaskViewHolder extends RecyclerView.ViewHolder  {
        public final TextView taskView;
        public final TextView dateView;
        CheckBox checkBox;

        public TaskViewHolder(View itemView){
            super(itemView);
            taskView = (TextView) itemView.findViewById(R.id.task);
            dateView = (TextView) itemView.findViewById(R.id.date);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

        }
     }

    /**
     *  Inflates the ViewHolder
     *  Sets the OnClickListener
     */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.tasklist_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    /**
     *  OnClickListener for the RecyclerView items
     */
    class MyClickListener implements View.OnClickListener {
        String task, date;
        int id, isComplete;

        public MyClickListener(int i, int ic, String t, String d){
            this.id = i;
            this.isComplete = ic;
            this.task = t;
            this.date = d;
        }
        public void onClick(View v) { }
    }

    /**
     * OnCheckedChangedListener for the CheckBox in TaskViewHolder
     */
    class MyCheckChangedListener implements CompoundButton.OnCheckedChangeListener{
        String task, date;
        int id, isComplete;

        public MyCheckChangedListener(int i, int ic, String t, String d){
            this.id = i;
            this.isComplete = ic;
            this.task = t;
            this.date = d;
        }
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { }
    }
    /**
     * Binds the database item to a TaskItem object
     *
     * @param holder - the TaskViewHolder that is being binded to data
     * @param position - position of holder
     */

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        if(!incompleteTasks.moveToPosition(position)) return;
        String task = incompleteTasks.getString(
                incompleteTasks.getColumnIndex(DatabaseHelper.KEY_TASK));
        String date = incompleteTasks.getString(
                incompleteTasks.getColumnIndex(DatabaseHelper.KEY_DATE));
        int id = incompleteTasks.getInt(
                incompleteTasks.getColumnIndex(DatabaseHelper.KEY_ID));
         int isComplete = incompleteTasks.getInt(
                incompleteTasks.getColumnIndex(DatabaseHelper.KEY_ISCOMPLETE));

        holder.taskView.setText(task);
        holder.dateView.setText(date);
        if(isComplete == 1) holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);

        // Set listener on the checkbox to update the database
        holder.checkBox.setOnCheckedChangeListener(
                new MyCheckChangedListener(id, isComplete, task, date){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                        if(isChecked) isComplete = 1;
                        else isComplete = 0;

                        MainActivity.DB.updateTask(id, isComplete, task);
                        ((Activity) mContext).recreate();
                    }
                }
        );

        final TaskViewHolder h = holder;

        // Set listener on the taskView to start an Edit Activity when clicked
        holder.taskView.setOnClickListener(
                new MyClickListener(id, isComplete, task, date){
                    @Override
                    public void onClick(View v){
                        Toast.makeText(mContext, "item clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, EditTaskActivity.class);
                        intent.putExtra(EXTRA_ID, id);
                        intent.putExtra(EXTRA_POSITION, h.getAdapterPosition());
                        intent.putExtra(EXTRA_TASK, task);
                        intent.putExtra(EXTRA_DATE, date);
                        intent.putExtra(EXTRA_ISCOMPLETE, isComplete);
                        ((Activity) mContext).startActivityForResult(intent, MainActivity.TASK_EDIT);
                    }
                }
        );
    }
    /**
     * Returns the number of complete tasks in the database
     */
    @Override
    public int getItemCount() {
        return incompleteTasks.getCount();
    }
}
