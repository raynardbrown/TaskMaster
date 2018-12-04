package com.example.android.taskmaster.model;

public class AttachmentExtraDataModel
{
  private String cardId;
  private String attachmentExtraData;
  private int attachmentIndex;
  private int attachmentExtraType;

  public AttachmentExtraDataModel(String cardId,
                                  String attachmentExtraData,
                                  int attachmentIndex,
                                  int attachmentExtraType)
  {
    this.cardId = cardId;
    this.attachmentExtraData = attachmentExtraData;
    this.attachmentIndex = attachmentIndex;
    this.attachmentExtraType = attachmentExtraType;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getAttachmentExtraData()
  {
    return attachmentExtraData;
  }

  public void setAttachmentExtraData(String attachmentExtraData)
  {
    this.attachmentExtraData = attachmentExtraData;
  }

  public int getAttachmentIndex()
  {
    return attachmentIndex;
  }

  public void setAttachmentIndex(int attachmentIndex)
  {
    this.attachmentIndex = attachmentIndex;
  }

  public int getAttachmentExtraType()
  {
    return attachmentExtraType;
  }

  public void setAttachmentExtraType(int attachmentExtraType)
  {
    this.attachmentExtraType = attachmentExtraType;
  }
}
