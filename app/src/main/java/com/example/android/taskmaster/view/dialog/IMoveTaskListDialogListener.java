package com.example.android.taskmaster.view.dialog;

public interface IMoveTaskListDialogListener
{
  /**
   * Triggered when the user confirms that they want to move the specified task list to another
   * task group.
   *
   * @param taskListIndex the index of the task list within the current task group
   *
   * @param currentTaskGroupIndex the index of the current task group
   *
   * @param newTaskGroupIndex the index of the task group where the specified task list shall be
   *                          moved.
   */
  public void onTaskListMoveClick(int taskListIndex,
                                  int currentTaskGroupIndex,
                                  int newTaskGroupIndex);
}
