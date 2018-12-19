package com.example.android.taskmaster.view.dialog;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskListSpinnerItem implements Parcelable
{
  private String taskListName;

  /**
   * A background color id from TaskMasterConstants. The color can be converted to actual color
   * value using TaskMasterUtils.colorIdToColorValue.
   */
  private int color;

  TaskListSpinnerItem(String taskListName, int color)
  {
    this.taskListName = taskListName;
    this.color = color;
  }

  protected TaskListSpinnerItem(Parcel in)
  {
    taskListName = in.readString();
    color = in.readInt();
  }

  public static final Creator<TaskListSpinnerItem> CREATOR = new Creator<TaskListSpinnerItem>()
  {
    @Override
    public TaskListSpinnerItem createFromParcel(Parcel in)
    {
      return new TaskListSpinnerItem(in);
    }

    @Override
    public TaskListSpinnerItem[] newArray(int size)
    {
      return new TaskListSpinnerItem[size];
    }
  };

  public String getTaskListName()
  {
    return taskListName;
  }

  public void setTaskListName(String taskListName)
  {
    this.taskListName = taskListName;
  }

  public int getColor()
  {
    return color;
  }

  public void setColor(int color)
  {
    this.color = color;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(taskListName);
    dest.writeInt(color);
  }
}
