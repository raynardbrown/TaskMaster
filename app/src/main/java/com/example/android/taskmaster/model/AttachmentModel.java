package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentModel implements Parcelable
{
  private String cardId;
  private String attachmentId;
  private String attachmentData;
  private int attachmentIndex;
  private long attachmentTime;
  private int attachmentType;
  private boolean bound;

  public AttachmentModel(String cardId,
                         String attachmentId,
                         String attachmentData,
                         int attachmentIndex,
                         long attachmentTime,
                         int attachmentType,
                         boolean bound)
  {
    this.cardId = cardId;
    this.attachmentId = attachmentId;
    this.attachmentData = attachmentData;
    this.attachmentIndex = attachmentIndex;
    this.attachmentTime = attachmentTime;
    this.attachmentType = attachmentType;
    this.bound = bound;
  }

  private AttachmentModel(Parcel in)
  {
    cardId = in.readString();
    attachmentId = in.readString();
    attachmentData = in.readString();
    attachmentIndex = in.readInt();
    attachmentTime = in.readLong();
    attachmentType = in.readInt();
    bound = in.readByte() != 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(cardId);
    dest.writeString(attachmentId);
    dest.writeString(attachmentData);
    dest.writeInt(attachmentIndex);
    dest.writeLong(attachmentTime);
    dest.writeInt(attachmentType);
    dest.writeByte((byte) (bound ? 1 : 0));
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public static final Creator<AttachmentModel> CREATOR = new Creator<AttachmentModel>()
  {
    @Override
    public AttachmentModel createFromParcel(Parcel in)
    {
      return new AttachmentModel(in);
    }

    @Override
    public AttachmentModel[] newArray(int size)
    {
      return new AttachmentModel[size];
    }
  };

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
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

  public boolean isBound()
  {
    return bound;
  }

  public void setBound(boolean bound)
  {
    this.bound = bound;
  }

}
