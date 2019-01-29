package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChecklistItemModel implements Parcelable
{
  private String checklistId;
  private String checklistItemId;
  private String itemTitle;
  private int itemIndex;
  private boolean completed;

  public ChecklistItemModel(String checklistId,
                            String checklistItemId,
                            String itemTitle,
                            int itemIndex,
                            boolean completed)
  {
    this.checklistId = checklistId;
    this.checklistItemId = checklistItemId;
    this.itemTitle = itemTitle;
    this.itemIndex = itemIndex;
    this.completed = completed;
  }

  protected ChecklistItemModel(Parcel in)
  {
    checklistId = in.readString();
    checklistItemId = in.readString();
    itemTitle = in.readString();
    itemIndex = in.readInt();
    completed = in.readByte() != 0;
  }

  public static final Creator<ChecklistItemModel> CREATOR = new Creator<ChecklistItemModel>()
  {
    @Override
    public ChecklistItemModel createFromParcel(Parcel in)
    {
      return new ChecklistItemModel(in);
    }

    @Override
    public ChecklistItemModel[] newArray(int size)
    {
      return new ChecklistItemModel[size];
    }
  };

  public String getChecklistId()
  {
    return checklistId;
  }

  public void setChecklistId(String checklistId)
  {
    this.checklistId = checklistId;
  }

  public String getChecklistItemId()
  {
    return checklistItemId;
  }

  public void setChecklistItemId(String checklistItemId)
  {
    this.checklistItemId = checklistItemId;
  }

  public String getItemTitle()
  {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle)
  {
    this.itemTitle = itemTitle;
  }

  public int getItemIndex()
  {
    return itemIndex;
  }

  public void setItemIndex(int itemIndex)
  {
    this.itemIndex = itemIndex;
  }

  public boolean isCompleted()
  {
    return completed;
  }

  public void setCompleted(boolean completed)
  {
    this.completed = completed;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(checklistId);
    dest.writeString(checklistItemId);
    dest.writeString(itemTitle);
    dest.writeInt(itemIndex);
    dest.writeByte((byte) (completed ? 1 : 0));
  }
}
