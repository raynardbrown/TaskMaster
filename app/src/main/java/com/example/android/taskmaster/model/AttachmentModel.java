package com.example.android.taskmaster.model;

public class AttachmentModel
{
  private String cardId;
  private String attachmentData;
  private int attachmentIndex;
  private long attachmentTime;
  private int attachmentType;

  public AttachmentModel(String cardId,
                         String attachmentData,
                         int attachmentIndex,
                         long attachmentTime,
                         int attachmentType)
  {
    this.cardId = cardId;
    this.attachmentData = attachmentData;
    this.attachmentIndex = attachmentIndex;
    this.attachmentTime = attachmentTime;
    this.attachmentType = attachmentType;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getAttachmentData()
  {
    return attachmentData;
  }

  public void setAttachmentData(String attachmentData)
  {
    this.attachmentData = attachmentData;
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

  public int getAttachmentType()
  {
    return attachmentType;
  }

  public void setAttachmentType(int attachmentType)
  {
    this.attachmentType = attachmentType;
  }
}
