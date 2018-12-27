package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskListModel implements Parcelable
{
  private final String taskGroupId;
  private final String taskListId;
  private final int taskIndex;
  private final String title;


  public TaskListModel(String taskGroupId,
                       String taskListId,
                       int taskIndex,
                       String title)
  {
    this.taskGroupId = taskGroupId;
    this.taskListId = taskListId;
    this.taskIndex = taskIndex;
    this.title = title;
  }

  protected TaskListModel(Parcel in)
  {
    taskGroupId = in.readString();
    taskListId = in.readString();
    taskIndex = in.readInt();
    title = in.readString();
  }

  public static final Creator<TaskListModel> CREATOR = new Creator<TaskListModel>()
  {
    @Override
    public TaskListModel createFromParcel(Parcel in)
    {
      return new TaskListModel(in);
    }

    @Override
    public TaskListModel[] newArray(int size)
    {
      return new TaskListModel[size];
    }
  };

  public String getTaskGroupId()
  {
    return taskGroupId;
  }

  public String getTaskListId()
  {
    return taskListId;
  }

  public int getTaskIndex()
  {
    return taskIndex;
  }

  public String getTitle()
  {
    return title;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(taskGroupId);
    dest.writeString(taskListId);
    dest.writeInt(taskIndex);
    dest.writeString(title);
  }
}
