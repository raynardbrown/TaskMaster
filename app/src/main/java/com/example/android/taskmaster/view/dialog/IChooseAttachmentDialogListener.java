package com.example.android.taskmaster.view.dialog;

public interface IChooseAttachmentDialogListener
{
  public void onNewAttachment(String attachmentPath,
                              int attachmentType,
                              String attachmentExtraPath,
                              int attachmentExtraType);

  public void onNewAttachmentDelayed();
}
