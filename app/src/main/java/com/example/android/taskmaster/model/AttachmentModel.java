package com.example.android.taskmaster.model;

public class AttachmentModel
{
  private String cardId;
  private String attachmentPath;
  private int attachmentIndex;
  private long attachmentTime;

  public AttachmentModel(String cardId,
                         String attachmentPath,
                         int attachmentIndex,
                         long attachmentTime)
  {
    this.cardId = cardId;
    this.attachmentPath = attachmentPath;
    this.attachmentIndex = attachmentIndex;
    this.attachmentTime = attachmentTime;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getAttachmentPath()
  {
    return attachmentPath;
  }

  public void setAttachmentPath(String attachmentPath)
  {
    this.attachmentPath = attachmentPath;
  }

  public int getAttachmentIndex()
  {
    return attachmentIndex;
  }

  public void setAttachmentIndex(int attachmentIndex)
  {
    this.attachmentIndex = attachmentIndex;
  }

  public long getAttachmentTime()
  {
    return attachmentTime;
  }

  public void setAttachmentTime(long attachmentTime)
  {
    this.attachmentTime = attachmentTime;
  }
}
