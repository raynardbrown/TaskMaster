package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskGroupListItemBinding;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.util.List;

public class TaskGroupListAdapter extends RecyclerView.Adapter<TaskGroupListAdapter.TaskGroupListViewHolder>
{
  private List<TaskGroupModel> taskGroupList;

  private ITaskGroupListItemClickListener listener;

  TaskGroupListAdapter(List<TaskGroupModel> taskGroupList,
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
    TaskGroupListItemBinding itemBinding = holder.itemBinding;

    int color = TaskMasterUtils.colorIdToColorValue(itemBinding.taskGroupListItemColorSquare.getContext(),
            taskGroupList.get(position).getColorKey());

    itemBinding.taskGroupListItemColorSquare.setBackgroundColor(color);

    itemBinding.tvTaskGroupListItemName.setText(taskGroupList.get(position).getTitle());
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
