package com.example.android.taskmaster.model;

import java.util.List;

public class ChecklistModel
{
  private String cardId;
  private String checklistId;
  private String checklistTitle;
  private int checklistIndex;
  private List<ChecklistItemModel> checklistItemModelList;

  public ChecklistModel(String cardId,
                        String checklistId,
                        String checklistTitle,
                        int checklistIndex,
                        List<ChecklistItemModel> checklistItemModelList)
  {
    this.cardId = cardId;
    this.checklistId = checklistId;
    this.checklistTitle = checklistTitle;
    this.checklistIndex = checklistIndex;
    this.checklistItemModelList = checklistItemModelList;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public String getChecklistId()
  {
    return checklistId;
  }

  public void setChecklistId(String checklistId)
  {
    this.checklistId = checklistId;
  }

  public String getChecklistTitle()
  {
    return checklistTitle;
  }

  public void setChecklistTitle(String checklistTitle)
  {
    this.checklistTitle = checklistTitle;
  }

  public int getChecklistIndex()
  {
    return checklistIndex;
  }

  public void setChecklistIndex(int checklistIndex)
  {
    this.checklistIndex = checklistIndex;
  }

  public List<ChecklistItemModel> getChecklistItemModelList()
  {
    return checklistItemModelList;
  }

  public void setChecklistItemModelList(List<ChecklistItemModel> checklistItemModelList)
  {
    this.checklistItemModelList = checklistItemModelList;
  }
}
