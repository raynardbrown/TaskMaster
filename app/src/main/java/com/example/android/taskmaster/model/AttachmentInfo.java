package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentInfo implements Parcelable
{
  public AttachmentModel boundAttachmentModel;
  public int attachmentCount;

  public AttachmentInfo()
  {
    this.boundAttachmentModel = null;
    this.attachmentCount = 0;
  }

  private AttachmentInfo(Parcel in)
  {
    boundAttachmentModel = in.readParcelable(AttachmentModel.class.getClassLoader());
    attachmentCount = in.readInt();
  }

  public static final Creator<AttachmentInfo> CREATOR = new Creator<AttachmentInfo>()
  {
    @Override
    public AttachmentInfo createFromParcel(Parcel in)
    {
      return new AttachmentInfo(in);
    }

    @Override
    public AttachmentInfo[] newArray(int size)
    {
      return new AttachmentInfo[size];
    }
  };

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeParcelable(boundAttachmentModel, flags);
    dest.writeInt(attachmentCount);
  }
}
