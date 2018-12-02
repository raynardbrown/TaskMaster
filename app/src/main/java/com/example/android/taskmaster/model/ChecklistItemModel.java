package com.example.android.taskmaster.model;

public class ChecklistItemModel
{
  private String checklistId;
  private String itemTitle;
  private int itemIndex;
  private boolean completed;

  public ChecklistItemModel(String checklistId,
                            String itemTitle,
                            int itemIndex,
                            boolean completed)
  {
    this.checklistId = checklistId;
    this.itemTitle = itemTitle;
    this.itemIndex = itemIndex;
    this.completed = completed;
  }

  public String getChecklistId()
  {
    return checklistId;
  }

  public void setChecklistId(String checklistId)
  {
    this.checklistId = checklistId;
  }

  public String getItemTitle()
  {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle)
  {
    this.itemTitle = itemTitle;
  }

  public int getItemIndex()
  {
    return itemIndex;
  }

  public void setItemIndex(int itemIndex)
  {
    this.itemIndex = itemIndex;
  }

  public boolean isCompleted()
  {
    return completed;
  }

  public void setCompleted(boolean completed)
  {
    this.completed = completed;
  }
}
