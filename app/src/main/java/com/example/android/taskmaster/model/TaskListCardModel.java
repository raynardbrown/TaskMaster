package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskListCardModel implements Parcelable
{
  private String taskGroupId;
  private String taskListId;
  private String cardId;
  private int taskIndex;
  private String cardTitle;
  private String cardDetailedDescription;
  private int cardIndex;

  public TaskListCardModel(String taskGroupId,
                    String taskListId,
                    String cardId,
                    int taskIndex,
                    String cardTitle,
                    String cardDetailedDescription,
                    int cardIndex)
  {
    this.taskGroupId = taskGroupId;
    this.taskListId = taskListId;
    this.cardId = cardId;
    this.taskIndex = taskIndex;
    this.cardTitle = cardTitle;
    this.cardDetailedDescription = cardDetailedDescription;
    this.cardIndex = cardIndex;
  }

  TaskListCardModel(Parcel in)
  {
    taskGroupId = in.readString();
    taskListId = in.readString();
    cardId = in.readString();
    taskIndex = in.readInt();
    cardTitle = in.readString();
    cardDetailedDescription = in.readString();
    cardIndex = in.readInt();
  }

  public static final Creator<TaskListCardModel> CREATOR = new Creator<TaskListCardModel>()
  {
    @Override
    public TaskListCardModel createFromParcel(Parcel in)
    {
      return new TaskListCardModel(in);
    }

    @Override
    public TaskListCardModel[] newArray(int size)
    {
      return new TaskListCardModel[size];
    }
  };

  public String getTaskGroupId()
  {
    return taskGroupId;
  }

  public void setTaskGroupId(String taskGroupId)
  {
    this.taskGroupId = taskGroupId;
  }

  public String getTaskListId()
  {
    return taskListId;
  }

  public void setTaskListId(String taskListId)
  {
    this.taskListId = taskListId;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public int getTaskIndex()
  {
    return taskIndex;
  }

  public void setTaskIndex(int taskIndex)
  {
    this.taskIndex = taskIndex;
  }

  public String getCardTitle()
  {
    return cardTitle;
  }

  public void setCardTitle(String cardTitle)
  {
    this.cardTitle = cardTitle;
  }

  public String getCardDetailedDescription()
  {
    return cardDetailedDescription;
  }

  public void setCardDetailedDescription(String cardDetailedDescription)
  {
    this.cardDetailedDescription = cardDetailedDescription;
  }

  public int getCardIndex()
  {
    return cardIndex;
  }

  public void setCardIndex(int cardIndex)
  {
    this.cardIndex = cardIndex;
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
    dest.writeString(cardId);
    dest.writeInt(taskIndex);
    dest.writeString(cardTitle);
    dest.writeString(cardDetailedDescription);
    dest.writeInt(cardIndex);
  }
}
