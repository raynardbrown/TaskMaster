package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentCreationInfo implements Parcelable
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

  protected AttachmentCreationInfo(Parcel in)
  {
    attachmentModel = in.readParcelable(AttachmentModel.class.getClassLoader());
    attachmentExtraDataModel = in.readParcelable(AttachmentExtraDataModel.class.getClassLoader());
    attachmentPath = in.readString();
    attachmentType = in.readInt();
    attachmentExtraPath = in.readString();
    attachmentExtraType = in.readInt();
  }

  public static final Creator<AttachmentCreationInfo> CREATOR = new Creator<AttachmentCreationInfo>()
  {
    @Override
    public AttachmentCreationInfo createFromParcel(Parcel in)
    {
      return new AttachmentCreationInfo(in);
    }

    @Override
    public AttachmentCreationInfo[] newArray(int size)
    {
      return new AttachmentCreationInfo[size];
    }
  };

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

  public void setAttachmentExtraDataModel(AttachmentExtraDataModel attachmentExtraDataModel)
  {
    this.attachmentExtraDataModel = attachmentExtraDataModel;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeParcelable(attachmentModel, flags);
    dest.writeParcelable(attachmentExtraDataModel, flags);
    dest.writeString(attachmentPath);
    dest.writeInt(attachmentType);
    dest.writeString(attachmentExtraPath);
    dest.writeInt(attachmentExtraType);
  }
}
