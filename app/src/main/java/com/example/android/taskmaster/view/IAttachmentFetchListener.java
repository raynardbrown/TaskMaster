package com.example.android.taskmaster.view;

public interface IAttachmentFetchListener
{
  /**
   * Called exactly once if there was at least one attachment fetched from a remote source.
   */
  public void onAttachment();
}
