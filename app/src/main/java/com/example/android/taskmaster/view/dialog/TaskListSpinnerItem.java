package com.example.android.taskmaster.view.dialog;

public class TaskListSpinnerItem
{
  private String taskListName;

  /**
   * A background color id from TaskMasterConstants. The color can be converted to actual color
   * value using TaskMasterUtils.colorIdToColorValue.
   */
  private int color;

  TaskListSpinnerItem(String taskListName, int color)
  {
    this.taskListName = taskListName;
    this.color = color;
  }

  public String getTaskListName()
  {
    return taskListName;
  }

  public void setTaskListName(String taskListName)
  {
    this.taskListName = taskListName;
  }

  public int getColor()
  {
    return color;
  }

  public void setColor(int color)
  {
    this.color = color;
  }
}
