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
  private List<ChecklistCompletionCounter> checklistCompletionCounterList;
  private List<AttachmentInfo> attachmentInfoList;
  private TaskListListAdapter taskListListAdapter;

  public TaskListModelContainer(TaskListModel taskListModel,
                                List<TaskListCardModel> cardList,
                                List<DueDateModel> dueDateModelList,
                                List<ChecklistCompletionCounter> checklistCompletionCounterList,
                                List<AttachmentInfo> attachmentInfoList)
  {
    this.taskListModel = taskListModel;
    this.cardList = cardList;
    this.dueDateModelList = dueDateModelList;
    this.checklistCompletionCounterList = checklistCompletionCounterList;
    this.attachmentInfoList = attachmentInfoList;
  }

  private TaskListModelContainer(Parcel in)
  {
    taskListModel = in.readParcelable(TaskListModel.class.getClassLoader());
    cardList = in.createTypedArrayList(TaskListCardModel.CREATOR);
    dueDateModelList = in.createTypedArrayList(DueDateModel.CREATOR);
    checklistCompletionCounterList = in.createTypedArrayList(ChecklistCompletionCounter.CREATOR);
    attachmentInfoList = in.createTypedArrayList(AttachmentInfo.CREATOR);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeParcelable(taskListModel, flags);
    dest.writeTypedList(cardList);
    dest.writeTypedList(dueDateModelList);
    dest.writeTypedList(checklistCompletionCounterList);
    dest.writeTypedList(attachmentInfoList);
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

  public List<ChecklistCompletionCounter> getChecklistCompletionCounterList()
  {
    return checklistCompletionCounterList;
  }

  public List<AttachmentInfo> getAttachmentInfoList()
  {
    return attachmentInfoList;
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
