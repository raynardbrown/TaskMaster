package com.example.android.taskmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.taskmaster.view.CardDetailChecklistDropDownAdapter;

import java.util.List;

public class ChecklistModelContainer implements Parcelable
{
  private ChecklistModel checklistModel;
  private List<ChecklistItemModel> checklistItemModelList;
  private CardDetailChecklistDropDownAdapter cardDetailChecklistDropDownAdapter;

  public ChecklistModelContainer(ChecklistModel checklistModel,
                                 List<ChecklistItemModel> checklistItemModelList)
  {
    this.checklistModel = checklistModel;
    this.checklistItemModelList = checklistItemModelList;
  }

  protected ChecklistModelContainer(Parcel in)
  {
    checklistModel = in.readParcelable(ChecklistModel.class.getClassLoader());
    checklistItemModelList = in.createTypedArrayList(ChecklistItemModel.CREATOR);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeParcelable(checklistModel, flags);
    dest.writeTypedList(checklistItemModelList);
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  public static final Creator<ChecklistModelContainer> CREATOR = new Creator<ChecklistModelContainer>()
  {
    @Override
    public ChecklistModelContainer createFromParcel(Parcel in)
    {
      return new ChecklistModelContainer(in);
    }

    @Override
    public ChecklistModelContainer[] newArray(int size)
    {
      return new ChecklistModelContainer[size];
    }
  };

  public ChecklistModel getChecklistModel()
  {
    return checklistModel;
  }

  public void setChecklistModel(ChecklistModel checklistModel)
  {
    this.checklistModel = checklistModel;
  }

  public List<ChecklistItemModel> getChecklistItemModelList()
  {
    return checklistItemModelList;
  }

  public void setChecklistItemModelList(List<ChecklistItemModel> checklistItemModelList)
  {
    this.checklistItemModelList = checklistItemModelList;
  }

  public CardDetailChecklistDropDownAdapter getCardDetailChecklistDropDownAdapter()
  {
    return cardDetailChecklistDropDownAdapter;
  }

  public void setCardDetailChecklistDropDownAdapter(CardDetailChecklistDropDownAdapter cardDetailChecklistDropDownAdapter)
  {
    this.cardDetailChecklistDropDownAdapter = cardDetailChecklistDropDownAdapter;
  }
}
