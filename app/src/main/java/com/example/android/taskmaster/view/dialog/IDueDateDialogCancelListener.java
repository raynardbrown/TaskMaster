package com.example.android.taskmaster.view.dialog;

public interface IDueDateDialogCancelListener
{
  /**
   * Trigger when the due date dialog was shown with a valid due, the due date was deleted by
   * the user in the dialog, but the user clicked cancel instead of done. We need to restore the
   * due date icon color within the activity.
   *
   * @param colorResId the color of the due date icon before the due date dialog was displayed.
   */
  public void onDueDateDialogCancel(int colorResId);
}
