package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.AttachmentItemBinding;

import java.util.List;

public class CardDetailAttachmentAdapter extends RecyclerView.Adapter<CardDetailAttachmentAdapter.CardDetailAttachmentAdapterViewHolder>
{
  private List<String> attachmentList;

  CardDetailAttachmentAdapter(List<String> attachmentList)
  {
    this.attachmentList = attachmentList;
  }

  @Override
  public CardDetailAttachmentAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.attachment_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    AttachmentItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new CardDetailAttachmentAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailAttachmentAdapterViewHolder holder, int position)
  {
    holder.itemBinding.tvAttachment.setText(attachmentList.get(position));

    // TODO: Do not hard code strings
    holder.itemBinding.tvAttachmentTime.setText("2 mins ago");
  }

  @Override
  public int getItemCount()
  {
    return attachmentList.size();
  }

  class CardDetailAttachmentAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private AttachmentItemBinding itemBinding;

    CardDetailAttachmentAdapterViewHolder(AttachmentItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
