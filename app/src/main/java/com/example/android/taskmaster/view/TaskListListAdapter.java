package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskMasterTaskListItemBinding;
import com.example.android.taskmaster.model.TaskListCardModel;

import java.util.Collections;
import java.util.List;

/**
 * Adapter for the list of cards within a task list in the TaskGroupActivity
 */
public class TaskListListAdapter extends RecyclerView.Adapter<TaskListListAdapter.TaskListListViewHolder>
{
  private List<TaskListCardModel> taskListItemList;

  private ITaskListCardClickDelegate taskListCardClickDelegate;
  private String taskListTitle;
  private TaskListAdapter taskListAdapter;
  private int taskListAdapterPosition;

  /**
   * The position within the list of the card that is currently being dragged.
   */
  private int draggedCardPosition;

  /**
   * Create a new TaskListListAdapter.
   *
   * @param taskListItemList the collection of cards that are in this TaskListListAdapter.
   *
   * @param taskListCardClickDelegate callback that is trigger when a card in this
   *                                  TaskListListAdapter is clicked.
   *
   * @param taskListTitle the title of the TaskList where this TaskListListAdapter belongs.
   *
   * @param taskListAdapter the parent adapter of this TaskListListAdapter.
   *
   * @param taskListAdapterPosition the index that this TaskListListAdapter holds within the
   *                                specified parent TaskListAdapter.
   *
   * @param draggedCardPosition the position within the list of the card being dragged or -1 if
   *                            there is no card being dragged.
   */
  TaskListListAdapter(List<TaskListCardModel> taskListItemList,
                      ITaskListCardClickDelegate taskListCardClickDelegate,
                      String taskListTitle,
                      TaskListAdapter taskListAdapter,
                      int taskListAdapterPosition,
                      int draggedCardPosition)
  {
    this.taskListItemList = taskListItemList;
    this.taskListCardClickDelegate = taskListCardClickDelegate;
    this.taskListTitle = taskListTitle;
    this.taskListAdapter = taskListAdapter;
    this.taskListAdapterPosition = taskListAdapterPosition;
    this.draggedCardPosition = draggedCardPosition;
  }

  @Override
  public TaskListListViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.task_master_task_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    TaskMasterTaskListItemBinding itemBinding = DataBindingUtil.inflate(inflater,
            layoutIdForListItem,
            parent,
            shouldAttachToParentImmediately);

    return new TaskListListViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(TaskListListViewHolder holder, int position)
  {
    TaskMasterTaskListItemBinding itemBinding = holder.itemBinding;

    if(position == draggedCardPosition)
    {
      // we are currently dragging the card at the specified position, ensure that the card is not
      // visible
      itemBinding.cvTaskListItemRoot.setVisibility(View.INVISIBLE);
    }
    else
    {
      // we are not dragging this card, make it visible
      itemBinding.cvTaskListItemRoot.setVisibility(View.VISIBLE);
    }

    itemBinding.cvTaskListItemRoot.setOnClickListener(new CardClickListener(holder));

    itemBinding.cvTaskListItemRoot.setOnLongClickListener(new CardLongClickListener(holder));

    itemBinding.tvTaskListItemShortDescription.setText(taskListItemList.get(position).getCardTitle());

    // TODO: Grab the attachment path from the database

    boolean anyIconsInRow = false;

    if(TextUtils.isEmpty(taskListItemList.get(position).getCardDetailedDescription()))
    {
      itemBinding.ivTaskListItemDetailedDescription.setVisibility(View.GONE);
    }
    else
    {
      itemBinding.ivTaskListItemDetailedDescription.setVisibility(View.VISIBLE);
      anyIconsInRow = true;
    }

    // TODO: Grab the attachment count from the database, display the image view and count accordingly

    // TODO: Grab the checklist count from the database, display the image view and completion ratio accordingly

    // TODO: Grab the due date from the database, display the image view and date accordingly (do not display completed due dates) set colors too

    if(anyIconsInRow)
    {
      // At least one item is visible, so the whole row must be visible
      itemBinding.clTaskListItemIconRow.setVisibility(View.VISIBLE);
    }
    else
    {
      itemBinding.clTaskListItemIconRow.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount()
  {
    return taskListItemList.size();
  }

  public List<TaskListCardModel> getList()
  {
    return taskListItemList;
  }

  /**
   * Notify this TaskListListAdapter that a drag operation has completed. Any dragged card that was
   * not visible during the drag operation shall be visible once more.
   */
  void notifyDragComplete()
  {
    int temp = draggedCardPosition;
    draggedCardPosition = -1;

    notifyItemChanged(temp);
  }

  void updateDragPosition(int newPosition)
  {
    draggedCardPosition = newPosition;
  }

  void onCardMoved(int fromPosition, int toPosition)
  {
    if(fromPosition < toPosition)
    {
      for(int i = fromPosition; i < toPosition; ++i)
      {
        Collections.swap(taskListItemList, i, i + 1);

        // update the card click listener positions
        taskListItemList.get(i).setCardIndex(i);
        taskListItemList.get(i + 1).setCardIndex(i + 1);

        // TODO: Update the database
      }
    }
    else
    {
      for(int i = fromPosition; i > toPosition; --i)
      {
        Collections.swap(taskListItemList, i, i - 1);

        taskListItemList.get(i).setCardIndex(i);
        taskListItemList.get(i - 1).setCardIndex(i - 1);

        // TODO: Update the database
      }
    }
    notifyItemMoved(fromPosition, toPosition);
  }

  class CardClickListener implements View.OnClickListener
  {
    private TaskListListViewHolder holder;

    CardClickListener(TaskListListViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      // Tell the delegate that the card at the specified position in the task list was clicked
      TaskListListAdapter.this.taskListCardClickDelegate.onTaskListCardClick(taskListTitle,
              taskListItemList.get(holder.getAdapterPosition()));
    }
  }

  class CardLongClickListener implements View.OnLongClickListener
  {
    private TaskListListViewHolder holder;

    CardLongClickListener(TaskListListViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public boolean onLongClick(View view)
    {
      View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

      // save the dragged card's position so we can make it visible once the drag has completed.
      draggedCardPosition = holder.getAdapterPosition();

      // tell the parent adapter that we have started a drag operation. The parent needs to be made
      // aware so that state will be consistent in the event that a task list within the parent is
      // recycled.
      taskListAdapter.notifyDragStart(taskListAdapterPosition, draggedCardPosition);

      view.startDrag(null,   // we won't use clip data, since only cards can be dragged in our use case.
              shadowBuilder, // use a default drag shadow
              null,          // Don't need the local state
              0);            // flags are not used

      // initially set the card view invisible. we will need to use the taskListAdapterPosition and
      // draggedCardPosition variables to maintain this state in case the parent (task list) is
      // recycled.
      view.setVisibility(View.INVISIBLE);

      return true;
    }
  }

  class TaskListListViewHolder extends RecyclerView.ViewHolder
  {
    private TaskMasterTaskListItemBinding itemBinding;

    TaskListListViewHolder(TaskMasterTaskListItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
