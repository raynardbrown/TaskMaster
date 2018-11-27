package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskMasterTaskListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the task lists within the TaskGroup Activity.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder>
{
  private List<String> taskListList;

  private final static String TAG = TaskListAdapter.class.getSimpleName();

  TaskListAdapter(List<String> taskListList)
  {
    this.taskListList = taskListList;
  }

  @Override
  public TaskListAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.task_master_task_list;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    TaskMasterTaskListBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new TaskListAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(TaskListAdapterViewHolder holder, int position)
  {
    TaskMasterTaskListBinding itemBinding = holder.itemBinding;

    if(position == taskListList.size())
    {
      // we are dealing with the add button
      // hide the root task list and show the add task list button
      itemBinding.clTaskListRootContainer.setVisibility(View.GONE);
      itemBinding.clTaskAddTaskListButtonContainer.setVisibility(View.VISIBLE);
    }
    else
    {
      // we are dealing with the task list
      // hide the add task list button and show the root task list
      itemBinding.clTaskAddTaskListButtonContainer.setVisibility(View.GONE);
      itemBinding.clTaskListRootContainer.setVisibility(View.VISIBLE);

      itemBinding.tvTaskListTitle.setText(taskListList.get(position));
      itemBinding.imgButtonTaskListMenu.setOnClickListener(new TaskListMenuClickListener());

      // TODO: Grab the cards
      setupItemRecyclerView(itemBinding, position);
    }
  }

  class TaskListMenuClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new TaskListMenuItemCLickListener());
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_task_list, popup.getMenu());
      popup.show();
    }
  }

  class TaskListMenuItemCLickListener implements PopupMenu.OnMenuItemClickListener
  {
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_move_task_list:
        {
          // TODO: Handle click
          return true;
        }

        default:
        {
          return false;
        }
      }
    }
  }

  private void setupItemRecyclerView(TaskMasterTaskListBinding itemBinding, int position)
  {
    Log.i(TaskListAdapter.TAG, "Setting up the recycler view at position " + (position + 1));
    LinearLayoutManager layoutManager = new LinearLayoutManager(itemBinding.getRoot().getContext());
    itemBinding.rvTaskList.setLayoutManager(layoutManager);

    List<String> cardList = new ArrayList<>();

    // TODO: Dummy data delete both blocks
    if(!((position % 2) == 0))
    {
      cardList.add(String.format("Description for card one in task list %d", (position + 1)));
      cardList.add(String.format("Description for card two in task list %d", (position + 1)));
      cardList.add(String.format("Description for card three in task list %d", (position + 1)));
      cardList.add(String.format("Description for card four in task list %d", (position + 1)));
      cardList.add(String.format("Description for card five in task list %d", (position + 1)));
      cardList.add(String.format("Description for card six in task list %d", (position + 1)));
      cardList.add(String.format("Description for card seven in task list %d", (position + 1)));
      cardList.add(String.format("Description for card eight in task list %d", (position + 1)));
      cardList.add(String.format("Description for card nine in task list %d", (position + 1)));
      cardList.add(String.format("Description for card ten in task list %d", (position + 1)));
    }
    else
    {
      cardList.add(String.format("Description for card one in task list %d", (position + 1)));
      cardList.add(String.format("Description for card two in task list %d", (position + 1)));
      cardList.add(String.format("Description for card three in task list %d", (position + 1)));
    }

    // create the adapter
    TaskListListAdapter adapter = new TaskListListAdapter(cardList);
    Log.i(TaskListAdapter.TAG, "Setting up the recycler adapter at position " + (position + 1));

    itemBinding.rvTaskList.setAdapter(adapter);

    Log.i(TaskListAdapter.TAG, "Added adapter to the recycler adapter at position " + (position + 1));
  }

  @Override
  public int getItemCount()
  {
    // A one is added to account for the add task list button that is displayed when no task lists
    // when tasks lists are displayed the add task list button is always at the end of that list
    return taskListList.size() + 1;
  }

  class TaskListAdapterViewHolder extends RecyclerView.ViewHolder
  {
    TaskMasterTaskListBinding itemBinding;
    TaskListAdapterViewHolder(TaskMasterTaskListBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
