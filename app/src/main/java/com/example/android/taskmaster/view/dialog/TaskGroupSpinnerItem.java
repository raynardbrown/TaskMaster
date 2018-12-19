package com.example.android.taskmaster.view.dialog;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskGroupSpinnerItem implements Parcelable
{
  private String taskGroupName;

  /**
   * A background color id from TaskMasterConstants. The color can be converted to actual color
   * value using TaskMasterUtils.colorIdToColorValue.
   */
  private int color;

  public TaskGroupSpinnerItem(String taskGroupName, int color)
  {
    this.taskGroupName = taskGroupName;
    this.color = color;
  }

  protected TaskGroupSpinnerItem(Parcel in)
  {
    taskGroupName = in.readString();
    color = in.readInt();
  }

  public static final Creator<TaskGroupSpinnerItem> CREATOR = new Creator<TaskGroupSpinnerItem>()
  {
    @Override
    public TaskGroupSpinnerItem createFromParcel(Parcel in)
    {
      return new TaskGroupSpinnerItem(in);
    }

    @Override
    public TaskGroupSpinnerItem[] newArray(int size)
    {
      return new TaskGroupSpinnerItem[size];
    }
  };

  public String getTaskGroupName()
  {
    return taskGroupName;
  }

  public void setTaskGroupName(String taskGroupName)
  {
    this.taskGroupName = taskGroupName;
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
    dest.writeString(taskGroupName);
    dest.writeInt(color);
  }
}
