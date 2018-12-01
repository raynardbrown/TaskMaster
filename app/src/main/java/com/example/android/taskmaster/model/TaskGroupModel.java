package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskGroupModel implements Parcelable
{
  private String id;
  private String title;
  private int colorKey;

  public TaskGroupModel(String id, String title, int colorKey)
  {
    this.id = id;
    this.title = title;
    this.colorKey = colorKey;
  }

  private TaskGroupModel(Parcel in)
  {
    id = in.readString();
    title = in.readString();
    colorKey = in.readInt();
  }

  public static final Creator<TaskGroupModel> CREATOR = new Creator<TaskGroupModel>()
  {
    @Override
    public TaskGroupModel createFromParcel(Parcel in)
    {
      return new TaskGroupModel(in);
    }

    @Override
    public TaskGroupModel[] newArray(int size)
    {
      return new TaskGroupModel[size];
    }
  };

  public String getId()
  {
    return id;
  }

  public String getTitle()
  {
    return title;
  }

  public int getColorKey()
  {
    return colorKey;
  }

  public void setColorKey(int newColorKey)
  {
    this.colorKey = newColorKey;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(id);
    dest.writeString(title);
    dest.writeInt(colorKey);
  }
}
