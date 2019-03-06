/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todowithcontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {


    private Context mContext;
    private Cursor taskCursor;

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_TASK = "TASK";
    public static final String EXTRA_DATE = "DATE";
    public static final String EXTRA_POSITION = "POSITION";
    public static final String EXTRA_ISCOMPLETE = "IS_COMPLETE";
    public static final String EXTRA_FROMRECYCLER = "FROM_RECYCLER";

    // Query parameters
    private String queryUri = Contract.CONTENT_URI.toString();
    private String queryCompleteUri = Contract.CONTENT_COMPLETE_URI.toString();
    private static final String[] projection = new String[] {Contract.CONTENT_PATH}; //table
    private String selectionClause = null;
    private String selectionArgs[] = null;
    private String sortOrder = "ASC";


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
     * Constructor for TaskAdapter
     *
     *
     * @param context
     */
    private final LayoutInflater inflater;
    public TaskAdapter(Context context, boolean viewingCompleted) {
        mContext = context;
        inflater = LayoutInflater.from(context);

        if(!viewingCompleted){
            // get a list of incomplete tasks
            taskCursor = mContext.getContentResolver().query(Uri.parse(queryUri), null,
                    null, null, sortOrder);
        } else{
            // get a list of complete tasks
            taskCursor = mContext.getContentResolver().query(Uri.parse(queryCompleteUri),
                    null, null, null, sortOrder);
        }
    }
    /**
     *  Inflates the ViewHolder
     *  Sets the OnClickListener
     */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // LayoutInflater inflater = LayoutInflater.from(mContext);
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

        String task = "";
        String date = "";
        int id = -1;
        int isComplete = 0;

        if(taskCursor != null){
            if(taskCursor.moveToPosition(position)){
                int indexTask = taskCursor.getColumnIndex(Contract.TaskList.KEY_TASK);
                task = taskCursor.getString(indexTask);


                Toast.makeText(mContext, "TASK: " + task, Toast.LENGTH_LONG);
                Log.d("TaskAdapter", "TASK: " + task + "\n");

                holder.taskView.setText(task);
                int indexDate = taskCursor.getColumnIndex(Contract.TaskList.KEY_DATE);
                date = taskCursor.getString(indexDate);
                holder.dateView.setText(date);
                int indexId = taskCursor.getColumnIndex(Contract.TaskList.KEY_ID);
                id = taskCursor.getInt(indexId);
                int indexIsComplete = taskCursor.getColumnIndex(Contract.TaskList.KEY_ISCOMPLETE);
                isComplete = taskCursor.getInt(indexIsComplete);
                if(isComplete == 1) holder.checkBox.setChecked(true);
                else holder.checkBox.setChecked(false);
            } else{
                return;
            }
        } else{
            Log.e("TaskAdapter", "onBindViewHolder: Cursor is null\n");
        }
        // Set listener on the checkbox to update the database
        holder.checkBox.setOnCheckedChangeListener(
                new MyCheckChangedListener(id, isComplete, task, date){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                        if(isChecked) isComplete = 1;
                        else isComplete = 0;
                        String[] selectionArgs = {Integer.toString(id)};
                        ContentValues values = new ContentValues();
                        values.put(Contract.TaskList.KEY_TASK, task);
                        //values.put(Contract.TaskList.KEY_ID, id);
                        values.put(Contract.TaskList.KEY_DATE, date);
                        values.put(Contract.TaskList.KEY_ISCOMPLETE, isComplete);
                        mContext.getContentResolver().update(Contract.CONTENT_URI, values,
                                Contract.TaskList.KEY_ID, selectionArgs);
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

        return taskCursor.getCount();

    }
}
