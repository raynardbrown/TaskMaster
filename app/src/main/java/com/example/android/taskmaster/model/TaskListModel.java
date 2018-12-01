package com.example.android.taskmaster.model;

import com.example.android.taskmaster.view.TaskListListAdapter;

import java.util.List;

public class TaskListModel
{
  private final String taskGroupId;
  private final String taskListId;
  private final int taskIndex;
  private final String title;
  private List<TaskListCardModel> cardList;
  private TaskListListAdapter adapter;

  public TaskListModel(String taskGroupId,
                       String taskListId,
                       int taskIndex,
                       String title,
                       List<TaskListCardModel> cardList)
  {
    this.taskGroupId = taskGroupId;
    this.taskListId = taskListId;
    this.taskIndex = taskIndex;
    this.title = title;
    this.cardList = cardList;
    this.adapter = adapter;
  }

  public String getTaskGroupId()
  {
    return taskGroupId;
  }

  public String getTaskListId()
  {
    return taskListId;
  }

  public int getTaskIndex()
  {
    return taskIndex;
  }

  public String getTitle()
  {
    return title;
  }

  public List<TaskListCardModel> getCardList()
  {
    return cardList;
  }

  public void setCardList(List<TaskListCardModel> cardList)
  {
    this.cardList = cardList;
  }

  public TaskListListAdapter getAdapter()
  {
    return adapter;
  }

  public void setAdapter(TaskListListAdapter adapter)
  {
    this.adapter = adapter;
  }
}
