package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistDropDownItemBinding;

import java.util.List;

public class CardDetailChecklistDropDownAdapter extends RecyclerView.Adapter<CardDetailChecklistDropDownAdapter.CardDetailChecklistDropDownAdapterViewHolder>
{
  private List<String> checklistList;

  CardDetailChecklistDropDownAdapter(List<String> checklistList)
  {
    this.checklistList = checklistList;
  }

  @Override
  public CardDetailChecklistDropDownAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.checklist_drop_down_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    ChecklistDropDownItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new CardDetailChecklistDropDownAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailChecklistDropDownAdapterViewHolder holder,
                               int position)
  {
    holder.itemBinding.etCardDetailActivityChecklistItemName.setText(checklistList.get(position));
  }

  @Override
  public int getItemCount()
  {
    return checklistList.size();
  }

  class CardDetailChecklistDropDownAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private ChecklistDropDownItemBinding itemBinding;

    CardDetailChecklistDropDownAdapterViewHolder(ChecklistDropDownItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
