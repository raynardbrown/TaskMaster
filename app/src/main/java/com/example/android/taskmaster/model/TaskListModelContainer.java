package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.taskmaster.view.TaskListListAdapter;

import java.util.List;

public class TaskListModelContainer implements Parcelable
{
  private TaskListModel taskListModel;
  private List<TaskListCardModel> cardList;
  private List<DueDateModel> dueDateModelList;
  private TaskListListAdapter taskListListAdapter;

  public TaskListModelContainer(TaskListModel taskListModel,
                                List<TaskListCardModel> cardList,
                                List<DueDateModel> dueDateModelList)
  {
    this.taskListModel = taskListModel;
    this.cardList = cardList;
    this.dueDateModelList = dueDateModelList;
  }

  protected TaskListModelContainer(Parcel in)
  {
    taskListModel = in.readParcelable(TaskListModel.class.getClassLoader());
    cardList = in.createTypedArrayList(TaskListCardModel.CREATOR);
    dueDateModelList = in.createTypedArrayList(DueDateModel.CREATOR);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeParcelable(taskListModel, flags);
    dest.writeTypedList(cardList);
    dest.writeTypedList(dueDateModelList);
  }

  @Override
  public int describeContents()
  {
    return 0;
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

  public List<DueDateModel> getDueDateModelList()
  {
    return dueDateModelList;
  }

  public TaskListListAdapter getTaskListListAdapter()
  {
    return taskListListAdapter;
  }

  public void setTaskListListAdapter(TaskListListAdapter taskListListAdapter)
  {
    this.taskListListAdapter = taskListListAdapter;
  }

}
