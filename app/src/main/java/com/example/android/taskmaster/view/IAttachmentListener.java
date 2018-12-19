package com.example.android.taskmaster.view;

import android.widget.ImageView;

public interface IAttachmentListener
{
  /**
   * The image attachment at the specified index was fully loaded.
   *
   * @param index index of the image attachment that was fully loaded.
   *
   * @param imageView the image view that contains the attachment
   */
  public void onImageAttachmentLoaded(int index, ImageView imageView);

  /**
   * Called when the attachment at the specified index has requested to be removed.
   *
   * @param index index of the attachment that wants to be removed.
   */
  public void onAttachmentRemoveRequest(int index);
}
