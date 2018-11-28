package com.example.android.taskmaster.view.dialog;

public interface IChooseBackgroundDialogListener
{
  /**
   * Called when the user chooses a background color from the choose background dialog.
   *
   * @param colorId one of the background color constants from TaskMasterConstants
   */
  public void onBackgroundSelected(int colorId);
}
