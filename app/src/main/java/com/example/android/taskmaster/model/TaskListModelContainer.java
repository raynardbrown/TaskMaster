package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.taskmaster.view.TaskListListAdapter;

import java.util.List;

public class TaskListModelContainer implements Parcelable
{
  private TaskListModel taskListModel;
  private List<TaskListCardModel> cardList;
  private TaskListListAdapter taskListListAdapter;

  public TaskListModelContainer(TaskListModel taskListModel,
                                List<TaskListCardModel> cardList)
  {
    this.taskListModel = taskListModel;
    this.cardList = cardList;
  }

  protected TaskListModelContainer(Parcel in)
  {
    cardList = in.createTypedArrayList(TaskListCardModel.CREATOR);
  }

  public static final Creator<TaskListModelContainer> CREATOR = new Creator<TaskListModelContainer>()
  {
    @Override
    public TaskListModelContainer createFromParcel(Parcel in)
    {
      return new TaskListModelContainer(in);
    }

    @Override
    public TaskListModelContainer[] newArray(int size)
    {
      return new TaskListModelContainer[size];
    }
  };

  public TaskListModel getTaskListModel()
  {
    return taskListModel;
  }

  public List<TaskListCardModel> getCardList()
  {
    return cardList;
  }

  public TaskListListAdapter getTaskListListAdapter()
  {
    return taskListListAdapter;
  }

  public void setTaskListListAdapter(TaskListListAdapter taskListListAdapter)
  {
    this.taskListListAdapter = taskListListAdapter;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeTypedList(cardList);
  }
}
