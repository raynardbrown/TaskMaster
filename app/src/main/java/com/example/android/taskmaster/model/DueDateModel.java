package com.example.android.taskmaster.model;

public class DueDateModel
{
  private String cardId;
  private long dueDate;
  private boolean completed;

  public DueDateModel(String cardId, long dueDate, boolean completed)
  {
    this.cardId = cardId;
    this.dueDate = dueDate;
    this.completed = completed;
  }

  public String getCardId()
  {
    return cardId;
  }

  public void setCardId(String cardId)
  {
    this.cardId = cardId;
  }

  public long getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(long dueDate)
  {
    this.dueDate = dueDate;
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
