package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskMasterTaskListBinding;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.model.TaskListCardModel;
import com.example.android.taskmaster.model.TaskListModelContainer;
import com.example.android.taskmaster.view.dialog.AddCardDialogFragment;
import com.example.android.taskmaster.view.dialog.AddTaskListDialogFragment;
import com.example.android.taskmaster.view.dialog.IAddCardDialogListener;
import com.example.android.taskmaster.view.dialog.MoveTaskListDialogFragment;
import com.example.android.taskmaster.view.dialog.TaskGroupSpinnerItem;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter for the task lists within the TaskGroupActivity.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder> implements IAddCardDialogListener
{
  private List<TaskListModelContainer> taskListList;

  private AppCompatActivity activity;

  private ITaskListCardClickDelegate taskListCardClickDelegate;

  private List<TaskGroupModel> taskGroupModelList;

  /**
   * The index of the task list in this TaskListAdapter whose card is being dragged or -1 if no card
   * is being dragged.
   */
  private int sourceDragTaskListIndex;

  /**
   * The index of the card that is being dragged. The card belongs to the task list at the index
   * specified by sourceDragTaskListIndex.
   */
  private int sourceDragCardIndex;

  TaskListAdapter(AppCompatActivity activity,
                  List<TaskListModelContainer> taskListList,
                  ITaskListCardClickDelegate taskListCardClickDelegate,
                  List<TaskGroupModel> taskGroupModelList)
  {
    this.activity = activity;
    this.taskListList = taskListList;
    this.taskListCardClickDelegate = taskListCardClickDelegate;
    this.taskGroupModelList = taskGroupModelList;
    this.sourceDragTaskListIndex = -1;
  }

  @Override
  public TaskListAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.task_master_task_list;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    TaskMasterTaskListBinding itemBinding = DataBindingUtil.inflate(inflater,
            layoutIdForListItem,
            parent,
            shouldAttachToParentImmediately);

    return new TaskListAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(TaskListAdapterViewHolder holder, int position)
  {
    TaskMasterTaskListBinding itemBinding = holder.itemBinding;

    if(position == taskListList.size())
    {
      // we are dealing with the add task list button
      // hide the root task list and show the add task list button
      itemBinding.clTaskListRootContainer.setVisibility(View.GONE);
      itemBinding.clTaskAddTaskListButtonContainer.setVisibility(View.VISIBLE);

      // set up click listener for the add task list button
      itemBinding.buttonTaskListAddTaskList.setOnClickListener(new AddTaskListClickListener());
    }
    else
    {
      bindViews(holder, position);
    }
  }

  @Override
  public int getItemCount()
  {
    // A one is added to account for the add task list button that is displayed when there are no
    // task lists. The add task list button is also displayed at the end of a list of task lists.
    return taskListList.size() + 1;
  }

  @Override
  public void onAddCardClick(String cardTitle, String detailedDescription, int position)
  {
    // Get the task list from the list at the specified position
    TaskListModelContainer taskListModelContainer = taskListList.get(position);

    // Add a new card to the task list
    List<TaskListCardModel> taskListCardModelList = taskListModelContainer.getCardList();

    TaskListCardModel taskListCardModel = new TaskListCardModel(taskListModelContainer.getTaskListModel().getTaskGroupId(),
            taskListModelContainer.getTaskListModel().getTaskListId(),
            UUID.randomUUID().toString(),
            position,
            cardTitle,
            detailedDescription,
            taskListCardModelList.size());

    taskListCardModelList.add(taskListCardModel);

    // Update the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListCardModelRoot = String.format("/%s/%s/", activity.getString(R.string.db_task_list_card_object_key), taskListCardModel.getCardId());

    String taskListTaskListCardRoot = String.format("/%s/%s/", activity.getString(R.string.db_task_list_task_list_card_key), taskListCardModel.getTaskListId());

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListCardModelRoot + activity.getString(R.string.db_task_list_card_title_key),
            taskListCardModel.getCardTitle());

    childUpdates.put(taskListCardModelRoot + activity.getString(R.string.db_task_list_card_detailed_description_key),
            taskListCardModel.getCardDetailedDescription());

    childUpdates.put(taskListCardModelRoot + activity.getString(R.string.db_task_list_card_index_key),
            taskListCardModel.getCardIndex());

    childUpdates.put(taskListCardModelRoot + activity.getString(R.string.db_task_list_card_task_index_key),
            taskListCardModel.getTaskIndex());

    // add the task list card to the collection of task lists in the task_list_task_list_card

    childUpdates.put(taskListTaskListCardRoot + taskListCardModel.getCardId(),
            taskListCardModel.getCardIndex());

    rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
            {
              @Override
              public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
              {
                if(databaseError == null)
                {
                  // write success
                  Log.i("TaskListAdap", "wrote task list card to database");
                }
                else
                {
                  // write failure
                  Log.i("TaskListAdap", "failed to write task list card to database");
                }
              }
            });

    // Notify the adapter
    taskListModelContainer.getTaskListListAdapter().notifyDataSetChanged();
  }

  /**
   * Notify this TaskListAdapter that a drag operation has started.
   *
   * @param taskListPosition the index of the task list whose card at the specified index is being
   *                         dragged.
   *
   * @param sourceDragCardIndex the index of the card within the task list at the specified task
   *                            list index that is being dragged.
   */
  void notifyDragStart(int taskListPosition, int sourceDragCardIndex)
  {
    this.sourceDragTaskListIndex = taskListPosition;
    this.sourceDragCardIndex = sourceDragCardIndex;
  }

  private void bindViews(TaskListAdapterViewHolder holder, int position)
  {
    TaskMasterTaskListBinding itemBinding = holder.itemBinding;

    // we are dealing with the task list
    // hide the add task list button and show the root task list
    itemBinding.clTaskAddTaskListButtonContainer.setVisibility(View.GONE);
    itemBinding.clTaskListRootContainer.setVisibility(View.VISIBLE);

    itemBinding.tvTaskListTitle.setText(taskListList.get(position).getTaskListModel().getTitle());
    itemBinding.imgButtonTaskListMenu.setOnClickListener(new TaskListMoveTaskListMenuClickListener(holder));

    // set up click listener for the add card button
    itemBinding.buttonTaskListAddCard.setOnClickListener(new TaskListAddCardClickListener(holder));

    setupItemRecyclerView(itemBinding, position);
  }

  private void setupItemRecyclerView(TaskMasterTaskListBinding itemBinding, int position)
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(itemBinding.getRoot().getContext());
    itemBinding.rvTaskList.setLayoutManager(layoutManager);

    TaskListModelContainer container = taskListList.get(position);

    int sourceDragCardIndex = -1;
    if(sourceDragTaskListIndex == position)
    {
      // we are currently dragging a card from this task list, send the card position to the card
      // adapter.
      sourceDragCardIndex = this.sourceDragCardIndex;
    }

    // create the card list adapter
    TaskListListAdapter taskListListAdapter = new TaskListListAdapter(container.getCardList(),
            taskListCardClickDelegate,
            container.getTaskListModel().getTitle(),
            this,
            position,
            sourceDragCardIndex);

    container.setTaskListListAdapter(taskListListAdapter);

    itemBinding.rvTaskList.setOnDragListener(new CardDragListener());

    itemBinding.rvTaskList.setAdapter(taskListListAdapter);
  }

  class TaskListAddCardClickListener implements View.OnClickListener
  {
    private TaskListAdapterViewHolder holder;

    TaskListAddCardClickListener(TaskListAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      AddCardDialogFragment dialogFragment = new AddCardDialogFragment();

      Bundle bundle = new Bundle();
      bundle.putInt(activity.getString(R.string.task_list_position_key), holder.getAdapterPosition());

      dialogFragment.setArguments(bundle);

      dialogFragment.show(activity.getSupportFragmentManager(),
              activity.getString(R.string.task_group_activity_dialog_add_card_tag_string));
    }
  }

  class AddTaskListClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      AddTaskListDialogFragment dialogFragment = new AddTaskListDialogFragment();

      dialogFragment.show(activity.getSupportFragmentManager(),
              activity.getString(R.string.task_group_activity_dialog_add_task_list_tag_string));
    }
  }

  class TaskListMoveTaskListMenuClickListener implements View.OnClickListener
  {
    private TaskListAdapterViewHolder holder;

    TaskListMoveTaskListMenuClickListener(TaskListAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new TaskListMoveTaskListMenuItemClickListener(holder));
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_task_list, popup.getMenu());
      popup.show();
    }
  }

  class TaskListMoveTaskListMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
  {
    private TaskListAdapterViewHolder holder;

    TaskListMoveTaskListMenuItemClickListener(TaskListAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_move_task_list:
        {
          List<TaskGroupSpinnerItem> taskGroupSpinnerItemList = new ArrayList<>();
          int currentTaskGroup = 0;

          TaskListModelContainer taskListModelContainer = taskListList.get(holder.getAdapterPosition());
          final String taskGroupId = taskListModelContainer.getTaskListModel().getTaskGroupId();

          for(int i = 0; i < taskGroupModelList.size(); ++i)
          {
            TaskGroupModel taskGroupModel = taskGroupModelList.get(i);
            taskGroupSpinnerItemList.add(new TaskGroupSpinnerItem(taskGroupModel.getTitle(),
                    taskGroupModel.getColorKey()));

            if(taskGroupModel.getId().equals(taskGroupId))
            {
              currentTaskGroup = i;
            }
          }

          MoveTaskListDialogFragment fragment = MoveTaskListDialogFragment.newInstance(activity,
                  taskGroupSpinnerItemList,
                  currentTaskGroup,
                  holder.getAdapterPosition());

          fragment.show(activity.getSupportFragmentManager());

          return true;
        }

        default:
        {
          return false;
        }
      }
    }
  }

  /**
   * This listener is attached to the recycler view within a task list widget so that a task list
   * can handle cards being dropped into the bounds of a task list.
   */
  class CardDragListener implements View.OnDragListener
  {
    CardDragListener()
    {

    }

    @Override
    public boolean onDrag(View v, DragEvent event)
    {
      switch (event.getAction())
      {
        case DragEvent.ACTION_DRAG_STARTED:
        {
          // return true so we get further drag events
          return true;
        }

        // we could be over the source recycler view or the target recycler view
        case DragEvent.ACTION_DRAG_LOCATION:
        {
          RecyclerView targetRecyclerView = (RecyclerView)v;

          // the view where the dragged card was dropped
          View childDroppedOn = targetRecyclerView.findChildViewUnder(event.getX(), event.getY());

          TaskListListAdapter targetTaskListListAdapter = (TaskListListAdapter) targetRecyclerView.getAdapter();
          TaskListListAdapter sourceTaskListListAdapter = taskListList.get(sourceDragTaskListIndex).getTaskListListAdapter();

          if(childDroppedOn != null)
          {
            int childDroppedOnPosition = targetRecyclerView.getChildAdapterPosition(childDroppedOn);

            int draggedCardViewInitialAdapterPosition = sourceDragCardIndex;

            // check to see if we are dealing with the same recycler view, i.e. we are doing
            // internal dragging and dropping
            if(sourceTaskListListAdapter == targetTaskListListAdapter)
            {
              if(targetTaskListListAdapter.getItemCount() - 2 == childDroppedOnPosition)
              {
                // scroll the recycler view to the last item
                targetRecyclerView.scrollToPosition(targetTaskListListAdapter.getItemCount() - 1);
              }
              else
              {
                // scroll the recycler view to the drop position
                targetRecyclerView.scrollToPosition(childDroppedOnPosition);
              }

              // swapped draggedCardView with the dropped on card view.
              targetTaskListListAdapter.onCardMoved(draggedCardViewInitialAdapterPosition, childDroppedOnPosition);

              // update dragged card position
              sourceDragCardIndex = childDroppedOnPosition;
              targetTaskListListAdapter.updateDragPosition(sourceDragCardIndex);
            }
          }
          break;
        }

        case DragEvent.ACTION_DRAG_ENTERED:
        {
          break;
        }

        case DragEvent.ACTION_DRAG_EXITED:
        {
          break;
        }

        case DragEvent.ACTION_DROP:
        {
          RecyclerView targetRecyclerView = (RecyclerView)v;

          // the view where the dragged card was dropped
          View childDroppedOn = targetRecyclerView.findChildViewUnder(event.getX(), event.getY());

          TaskListListAdapter targetTaskListListAdapter = (TaskListListAdapter) targetRecyclerView.getAdapter();
          TaskListListAdapter sourceTaskListListAdapter = taskListList.get(sourceDragTaskListIndex).getTaskListListAdapter();

          int draggedCardViewInitialAdapterPosition = sourceDragCardIndex;

          if(childDroppedOn != null)
          {
            int childDroppedOnPosition = targetRecyclerView.getChildAdapterPosition(childDroppedOn);

            // check to see if we are dealing with the same recycler view, i.e. we are doing internal dragging and dropping
            if(sourceTaskListListAdapter == targetTaskListListAdapter)
            {
              // swapped draggedCardView with the dropped on card view.
              targetTaskListListAdapter.onCardMoved(draggedCardViewInitialAdapterPosition, childDroppedOnPosition);
            }
            else
            {
              // drop card on a different recycler view
              dropCardOnRecyclerView(draggedCardViewInitialAdapterPosition,
                      childDroppedOnPosition,
                      sourceTaskListListAdapter,
                      targetRecyclerView);
            }
          }
          else
          {
            // drop the card onto an empty recycler view
            dropCardOnEmptyRecyclerView(draggedCardViewInitialAdapterPosition,
                    sourceTaskListListAdapter,
                    targetRecyclerView);
          }

          return true;
        }

        case DragEvent.ACTION_DRAG_ENDED:
        {
          // set the dragged card view visible once more
          if(TaskListAdapter.this.sourceDragTaskListIndex != -1)
          {
            TaskListModelContainer container = taskListList.get(TaskListAdapter.this.sourceDragTaskListIndex);

            // tell the child adapter that we are done dragging.
            container.getTaskListListAdapter().notifyDragComplete();

            TaskListAdapter.this.sourceDragTaskListIndex = -1;
          }
        }

        default:
        {
          break;
        }
      }
      return false;
    }

    private void dropCardOnRecyclerView(int draggedCardViewInitialAdapterPosition,
                                        int childDroppedOnPosition,
                                        TaskListListAdapter sourceTaskListListAdapter,
                                        RecyclerView targetRecyclerView)
    {
      // remove the dragged card from the source/dragged recycler view
      TaskListCardModel droppedTaskListCardModel = sourceTaskListListAdapter.getList().remove(draggedCardViewInitialAdapterPosition);

      // notify the source of the changes
      sourceTaskListListAdapter.notifyDataSetChanged();

      // Target

      TaskListListAdapter targetTaskListListAdapter = (TaskListListAdapter)targetRecyclerView.getAdapter();
      List<TaskListCardModel> taskListCardModelList = targetTaskListListAdapter.getList();

      // Add the dropped model to the target recycler view
      taskListCardModelList.add(droppedTaskListCardModel);

      // move all of the positions of the target recycler view items to the left by one ending at the dropped child position
      for(int i = childDroppedOnPosition; i > taskListCardModelList.size(); --i)
      {
        Collections.swap(taskListCardModelList, i, i - 1);
      }

      // notify the target of the changes
      targetTaskListListAdapter.notifyDataSetChanged();
    }

    private void dropCardOnEmptyRecyclerView(int draggedCardViewInitialAdapterPosition,
                                             TaskListListAdapter sourceTaskListListAdapter,
                                             RecyclerView targetRecyclerView)
    {
      // remove the dragged card from the source/dragged recycler view
      TaskListCardModel droppedTaskListCardModel = sourceTaskListListAdapter.getList().remove(draggedCardViewInitialAdapterPosition);

      // notify the source of the changes
      sourceTaskListListAdapter.notifyDataSetChanged();

      // Target

      TaskListListAdapter targetTaskListListAdapter = (TaskListListAdapter)targetRecyclerView.getAdapter();
      List<TaskListCardModel> taskListCardModelList = targetTaskListListAdapter.getList();

      // Add the dropped model to the target recycler view
      taskListCardModelList.add(droppedTaskListCardModel);

      // notify the target of the changes
      targetTaskListListAdapter.notifyDataSetChanged();
    }
  }

  class TaskListAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private TaskMasterTaskListBinding itemBinding;

    TaskListAdapterViewHolder(TaskMasterTaskListBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
