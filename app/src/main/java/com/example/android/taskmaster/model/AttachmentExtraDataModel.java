package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentExtraDataModel implements Parcelable
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

  protected AttachmentExtraDataModel(Parcel in)
  {
    cardId = in.readString();
    attachmentExtraData = in.readString();
    attachmentIndex = in.readInt();
    attachmentExtraType = in.readInt();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(cardId);
    dest.writeString(attachmentExtraData);
    dest.writeInt(attachmentIndex);
    dest.writeInt(attachmentExtraType);
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public static final Creator<AttachmentExtraDataModel> CREATOR = new Creator<AttachmentExtraDataModel>()
  {
    @Override
    public AttachmentExtraDataModel createFromParcel(Parcel in)
    {
      return new AttachmentExtraDataModel(in);
    }

    @Override
    public AttachmentExtraDataModel[] newArray(int size)
    {
      return new AttachmentExtraDataModel[size];
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
