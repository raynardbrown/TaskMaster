package com.example.android.taskmaster.view;

public interface IChecklistItemClickListener
{
  public void onChecklistItemClick(String actionBarTitle,
                                   boolean showCommitButton,
                                   IEditCompleteListener editCompleteListener);
}
