package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChecklistCompletionCounter implements Parcelable
{
  public int totalChecklistItems;
  public int totalChecked;

  public ChecklistCompletionCounter()
  {
    totalChecklistItems = 0;
    totalChecked = 0;
  }

  private ChecklistCompletionCounter(Parcel in)
  {
    totalChecklistItems = in.readInt();
    totalChecked = in.readInt();
  }

  public static final Creator<ChecklistCompletionCounter> CREATOR = new Creator<ChecklistCompletionCounter>()
  {
    @Override
    public ChecklistCompletionCounter createFromParcel(Parcel in)
    {
      return new ChecklistCompletionCounter(in);
    }

    @Override
    public ChecklistCompletionCounter[] newArray(int size)
    {
      return new ChecklistCompletionCounter[size];
    }
  };

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(totalChecklistItems);
    dest.writeInt(totalChecked);
  }
}
