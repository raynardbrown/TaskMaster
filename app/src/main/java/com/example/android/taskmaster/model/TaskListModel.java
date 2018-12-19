package com.example.android.taskmaster.model;

public class TaskListModel
{
  private final String taskGroupId;
  private final String taskListId;
  private final int taskIndex;
  private final String title;


  public TaskListModel(String taskGroupId,
                       String taskListId,
                       int taskIndex,
                       String title)
  {
    this.taskGroupId = taskGroupId;
    this.taskListId = taskListId;
    this.taskIndex = taskIndex;
    this.title = title;
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
}
