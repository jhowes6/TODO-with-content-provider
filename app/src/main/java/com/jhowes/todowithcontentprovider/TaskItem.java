/**
 * Written by Jason Howes
 * 2019
 *
 */

package com.jhowes.todowithcontentprovider;


public class TaskItem {

    private int id;
    private String task;
    private String date;
    private int isComplete;

    public TaskItem(String task,  int isComplete) {
        this.task = task;
        this.isComplete = isComplete;
    }
    public TaskItem(){}

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    public int isComplete() {
        return isComplete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComplete(int complete) {
        isComplete = complete;
    }
}
