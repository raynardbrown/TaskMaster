package com.example.android.taskmaster.model;

public class AttachmentCreationInfo
{
  private AttachmentModel attachmentModel;
  private AttachmentExtraDataModel attachmentExtraDataModel;
  private String attachmentPath;
  private int attachmentType;
  private String attachmentExtraPath;
  private int attachmentExtraType;

  public AttachmentCreationInfo(AttachmentModel attachmentModel,
                                AttachmentExtraDataModel attachmentExtraDataModel,
                                String attachmentPath,
                                int attachmentType,
                                String attachmentExtraPath,
                                int attachmentExtraType)
  {
    this.attachmentModel = attachmentModel;
    this.attachmentExtraDataModel = attachmentExtraDataModel;
    this.attachmentPath = attachmentPath;
    this.attachmentType = attachmentType;
    this.attachmentExtraPath = attachmentExtraPath;
    this.attachmentExtraType = attachmentExtraType;
  }

  public AttachmentModel getAttachmentModel()
  {
    return attachmentModel;
  }

  public void setAttachmentModel(AttachmentModel attachmentModel)
  {
    this.attachmentModel = attachmentModel;
  }

  public String getAttachmentPath()
  {
    return attachmentPath;
  }

  public void setAttachmentPath(String attachmentPath)
  {
    this.attachmentPath = attachmentPath;
  }

  public int getAttachmentType()
  {
    return attachmentType;
  }

  public void setAttachmentType(int attachmentType)
  {
    this.attachmentType = attachmentType;
  }

  public String getAttachmentExtraPath()
  {
    return attachmentExtraPath;
  }

  public void setAttachmentExtraPath(String attachmentExtraPath)
  {
    this.attachmentExtraPath = attachmentExtraPath;
  }

  public int getAttachmentExtraType()
  {
    return attachmentExtraType;
  }

  public void setAttachmentExtraType(int attachmentExtraType)
  {
    this.attachmentExtraType = attachmentExtraType;
  }

  public AttachmentExtraDataModel getAttachmentExtraDataModel()
  {
    return attachmentExtraDataModel;
  }

  public void setAttachmentExtraDataModel(
          AttachmentExtraDataModel attachmentExtraDataModel)
  {
    this.attachmentExtraDataModel = attachmentExtraDataModel;
  }
}
