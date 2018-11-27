package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskMasterTaskListItemBinding;

import java.util.List;

/**
 * Adapter for the list within a task list in the TaskGroup activity
 */
public class TaskListListAdapter extends RecyclerView.Adapter<TaskListListAdapter.TaskListListViewViewHolder>
{
  private List<String> taskListItemList;

  private static final String TAG = TaskListListAdapter.class.getSimpleName();

  TaskListListAdapter(List<String> taskListItemList)
  {
    this.taskListItemList = taskListItemList;
  }

  @Override
  public TaskListListViewViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.task_master_task_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    TaskMasterTaskListItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new TaskListListViewViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(TaskListListViewViewHolder holder, int position)
  {
    TaskMasterTaskListItemBinding itemBinding = holder.itemBinding;

    itemBinding.tvTaskListItemShortDescription.setText(taskListItemList.get(position));

    // TODO: Fill in the rest

    // TODO: For now just add a few test items
    if(position == 1)
    {
      itemBinding.ivTaskListItemCard.setVisibility(View.GONE);
    }
    else if(!((position % 2) == 0))
    {
      itemBinding.ivTaskListItemCard.setImageResource(R.drawable.architecture_building_driveway_186077_small);
      itemBinding.ivTaskListItemCard.setVisibility(View.VISIBLE);
    }
    else
    {
      itemBinding.ivTaskListItemCard.setVisibility(View.GONE);
    }

    // TODO: Testing only. You need to determine whether these icons should be displayed or not
    // TODO: You also need to determine what values will be in the icon's text view
    itemBinding.tvTaskListItemAttachmentCount.setText("10");
    itemBinding.tvTaskListItemChecklistCompletionRatio.setText("2/3");
    itemBinding.tvTaskListItemDueDate.setText("Nov 30");
  }

  @Override
  public int getItemCount()
  {
    Log.i(TaskListListAdapter.TAG, "getItemCount: " + taskListItemList.size());
    return taskListItemList.size();
  }

  class TaskListListViewViewHolder extends RecyclerView.ViewHolder
  {
    TaskMasterTaskListItemBinding itemBinding;

    TaskListListViewViewHolder(TaskMasterTaskListItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
