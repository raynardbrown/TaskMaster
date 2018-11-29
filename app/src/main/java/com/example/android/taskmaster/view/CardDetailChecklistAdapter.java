package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistItemBinding;

import java.util.ArrayList;
import java.util.List;

public class CardDetailChecklistAdapter extends RecyclerView.Adapter<CardDetailChecklistAdapter.CardDetailChecklistAdapterViewHolder>
{
  private List<String> checklistList;

  CardDetailChecklistAdapter(List<String> checklistList)
  {
    this.checklistList = checklistList;
  }

  @Override
  public CardDetailChecklistAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.checklist_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    ChecklistItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new CardDetailChecklistAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailChecklistAdapterViewHolder holder, int position)
  {
    holder.itemBinding.tvChecklistName.setText(checklistList.get(position));

    setupCheckListItemRecyclerView(holder.itemBinding);
  }

  private void setupCheckListItemRecyclerView(ChecklistItemBinding itemBinding)
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(itemBinding.getRoot().getContext());
    itemBinding.rvChecklist.setLayoutManager(layoutManager);

    List<String> checkListItems = new ArrayList<>();

    // TODO: Dummy string delete
    checkListItems.add("Checklist item one");
    checkListItems.add("Checklist item two");
    checkListItems.add("Checklist item three");
    checkListItems.add("Checklist item four");

    CardDetailChecklistDropDownAdapter adapter = new CardDetailChecklistDropDownAdapter(checkListItems);
    itemBinding.rvChecklist.setAdapter(adapter);

    ViewCompat.setNestedScrollingEnabled(itemBinding.rvChecklist, false);
  }

  @Override
  public int getItemCount()
  {
    return checklistList.size();
  }

  class CardDetailChecklistAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private ChecklistItemBinding itemBinding;

    CardDetailChecklistAdapterViewHolder(ChecklistItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
