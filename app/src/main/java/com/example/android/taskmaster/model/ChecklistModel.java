package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChecklistModel implements Parcelable
{
  private String cardId;
  private String checklistId;
  private String checklistTitle;
  private int checklistIndex;
  private boolean collapsed;

  public ChecklistModel(String cardId,
                        String checklistId,
                        String checklistTitle,
                        int checklistIndex,
                        boolean collapsed)
  {
    this.cardId = cardId;
    this.checklistId = checklistId;
    this.checklistTitle = checklistTitle;
    this.checklistIndex = checklistIndex;
    this.collapsed = collapsed;
  }

  protected ChecklistModel(Parcel in)
  {
    cardId = in.readString();
    checklistId = in.readString();
    checklistTitle = in.readString();
    checklistIndex = in.readInt();
    collapsed = in.readByte() != 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(cardId);
    dest.writeString(checklistId);
    dest.writeString(checklistTitle);
    dest.writeInt(checklistIndex);
    dest.writeByte((byte) (collapsed ? 1 : 0));
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public static final Creator<ChecklistModel> CREATOR = new Creator<ChecklistModel>()
  {
    @Override
    public ChecklistModel createFromParcel(Parcel in)
    {
      return new ChecklistModel(in);
    }

    @Override
    public ChecklistModel[] newArray(int size)
    {
      return new ChecklistModel[size];
    }
  };

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getChecklistId()
  {
    return checklistId;
  }

  public void setChecklistId(String checklistId)
  {
    this.checklistId = checklistId;
  }

  public String getChecklistTitle()
  {
    return checklistTitle;
  }

  public void setChecklistTitle(String checklistTitle)
  {
    this.checklistTitle = checklistTitle;
  }

  public int getChecklistIndex()
  {
    return checklistIndex;
  }

  public void setChecklistIndex(int checklistIndex)
  {
    this.checklistIndex = checklistIndex;
  }

  public boolean isCollapsed()
  {
    return collapsed;
  }

  public void setCollapsed(boolean collapsed)
  {
    this.collapsed = collapsed;
  }
}
