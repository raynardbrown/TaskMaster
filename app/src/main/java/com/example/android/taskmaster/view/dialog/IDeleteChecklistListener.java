package com.example.android.taskmaster.view.dialog;

public interface IDeleteChecklistListener
{
  /**
   * Triggered when there is a request to delete the checklist at the specified index within the
   * card detail activity.
   *
   * @param checklistIndex the index of the checklist within the card detail activity.
   */
  public void onChecklistDelete(int checklistIndex);
}
