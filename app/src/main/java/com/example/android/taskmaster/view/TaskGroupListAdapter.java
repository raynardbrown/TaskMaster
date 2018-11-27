package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskGroupListItemBinding;

import java.util.List;

public class TaskGroupListAdapter extends  RecyclerView.Adapter<TaskGroupListAdapter.TaskGroupListViewHolder>
{
  private List<String> taskGroupList;

  private ITaskGroupListItemClickListener listener;

  TaskGroupListAdapter(List<String> taskGroupList,
                       ITaskGroupListItemClickListener listener)
  {
    this.taskGroupList = taskGroupList;
    this.listener = listener;
  }

  @Override
  public TaskGroupListViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.task_group_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    TaskGroupListItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new TaskGroupListViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(TaskGroupListViewHolder holder, int position)
  {
    // TODO: Don't hard code color
    int backgroundColor = Color.argb(255, 0, 0, 255);
    holder.itemBinding.taskGroupListItemColorSquare.setBackgroundColor(backgroundColor);
    holder.itemBinding.tvTaskGroupListItemName.setText(taskGroupList.get(position));
  }

  @Override
  public int getItemCount()
  {
    return taskGroupList.size();
  }

  class TaskGroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
  {
    private TaskGroupListItemBinding itemBinding;

    TaskGroupListViewHolder(TaskGroupListItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;

      this.itemBinding.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
      final int clickedIndex = getAdapterPosition();

      TaskGroupListAdapter.this.listener.onTaskGroupListItemClick(clickedIndex);
    }
  }
}
