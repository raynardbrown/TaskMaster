package com.example.android.taskmaster.view.dialog;

public interface IForgotPasswordDialogListener
{
  /**
   * Triggered when the user enters an email address into the dialog and presses the send password
   * button.
   *
   * @param emailAddress the email address that the user entered into the email address field of the
   *                     dialog.
   */
  public void onSendPasswordClick(String emailAddress);
}
