package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DueDateModel implements Parcelable
{
  private String cardId;
  private long dueDate;
  private boolean completed;

  public DueDateModel(String cardId, long dueDate, boolean completed)
  {
    this.cardId = cardId;
    this.dueDate = dueDate;
    this.completed = completed;
  }

  protected DueDateModel(Parcel in)
  {
    cardId = in.readString();
    dueDate = in.readLong();
    completed = in.readByte() != 0;
  }

  public static final Creator<DueDateModel> CREATOR = new Creator<DueDateModel>()
  {
    @Override
    public DueDateModel createFromParcel(Parcel in)
    {
      return new DueDateModel(in);
    }

    @Override
    public DueDateModel[] newArray(int size)
    {
      return new DueDateModel[size];
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

  public long getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(long dueDate)
  {
    this.dueDate = dueDate;
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
    dest.writeString(cardId);
    dest.writeLong(dueDate);
    dest.writeByte((byte) (completed ? 1 : 0));
  }
}
