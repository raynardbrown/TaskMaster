package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.TaskMasterTaskListItemBinding;
import com.example.android.taskmaster.db.FirebaseRealtimeDbProvider;
import com.example.android.taskmaster.model.AttachmentInfo;
import com.example.android.taskmaster.model.AttachmentModel;
import com.example.android.taskmaster.model.ChecklistCompletionCounter;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskListCardModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayInputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the list of cards within a task list in the TaskGroupActivity
 */
public class TaskListListAdapter extends RecyclerView.Adapter<TaskListListAdapter.TaskListListViewHolder>
{
  private List<TaskListCardModel> taskListItemList;

  private ITaskListCardClickDelegate taskListCardClickDelegate;
  private String taskListTitle;
  private String taskListId;
  private TaskListAdapter taskListAdapter;
  private int taskListAdapterPosition;

  /**
   * The position within the list of the card that is currently being dragged.
   */
  private int draggedCardPosition;
  private List<DueDateModel> dueDateModelList;
  private List<ChecklistCompletionCounter> checklistCompletionCounterList;
  private List<AttachmentInfo> attachmentInfoList;

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
   * @param taskListId the identifier of the TaskList where this TaskListListAdapter belongs.
   *
   * @param taskListAdapter the parent adapter of this TaskListListAdapter.
   *
   * @param taskListAdapterPosition the index that this TaskListListAdapter holds within the
   *                                specified parent TaskListAdapter.
   *
   * @param draggedCardPosition the position within the list of the card being dragged or -1 if
   *                            there is no card being dragged.
   *
   * @param dueDateModelList the list of due dates associated with each card. An entry in this list
   *                         could be null if the card a the same index does not have a due date.
   *
   * @param checklistCompletionCounterList list containing a completion ratio of the checklists for
   *                                       a given card or null if the card does not have a
   *                                       checklist.
   *
   * @param attachmentInfoList list containing the count of attachments for a given card or null if
   *                           a card does not have a checklist. If a given card does have a
   *                           attachments then the list also contains a bound attachment or null
   *                           if a given card does not have any attachments that are bound.
   */
  TaskListListAdapter(List<TaskListCardModel> taskListItemList,
                      ITaskListCardClickDelegate taskListCardClickDelegate,
                      String taskListTitle,
                      String taskListId,
                      TaskListAdapter taskListAdapter,
                      int taskListAdapterPosition,
                      int draggedCardPosition,
                      List<DueDateModel> dueDateModelList,
                      List<ChecklistCompletionCounter> checklistCompletionCounterList,
                      List<AttachmentInfo> attachmentInfoList)
  {
    this.taskListItemList = taskListItemList;
    this.taskListCardClickDelegate = taskListCardClickDelegate;
    this.taskListTitle = taskListTitle;
    this.taskListId = taskListId;
    this.taskListAdapter = taskListAdapter;
    this.taskListAdapterPosition = taskListAdapterPosition;
    this.draggedCardPosition = draggedCardPosition;
    this.dueDateModelList = dueDateModelList;
    this.checklistCompletionCounterList = checklistCompletionCounterList;
    this.attachmentInfoList = attachmentInfoList;
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

    // Grab the attachment count from the database, display the image view and count accordingly
    if(attachmentInfoList.size() > 0)
    {
      AttachmentInfo attachmentInfo = attachmentInfoList.get(position);

      if(attachmentInfo != null)
      {
        itemBinding.ivTaskListItemAttachment.setVisibility(View.VISIBLE);
        itemBinding.tvTaskListItemAttachmentCount.setVisibility(View.VISIBLE);

        String attachmentCountAsString = String.format(Locale.getDefault(), "%d",
                attachmentInfo.attachmentCount);

        itemBinding.tvTaskListItemAttachmentCount.setText(attachmentCountAsString);

        AttachmentModel attachmentModel = attachmentInfo.boundAttachmentModel;
        if(attachmentModel != null)
        {
          itemBinding.ivTaskListItemCard.setVisibility(View.VISIBLE);

          byte[] base64AttachmentData = Base64.decode(attachmentModel.getAttachmentData(),
                  Base64.NO_WRAP);

          Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(base64AttachmentData));

          itemBinding.ivTaskListItemCard.setImageBitmap(bitmap);
        }
        else
        {
          itemBinding.ivTaskListItemCard.setVisibility(View.GONE);
        }

        anyIconsInRow = true;
      }
      else
      {
        itemBinding.ivTaskListItemAttachment.setVisibility(View.GONE);
        itemBinding.tvTaskListItemAttachmentCount.setVisibility(View.GONE);
      }
    }

    // Grab the checklist count, display the image view and completion ratio accordingly
    if(checklistCompletionCounterList.size() > 0)
    {
      ChecklistCompletionCounter checklistCompletionCounter = checklistCompletionCounterList.get(position);
      if(checklistCompletionCounter != null && checklistCompletionCounter.totalChecklistItems > 0)
      {
        itemBinding.ivTaskListItemChecklist.setVisibility(View.VISIBLE);
        itemBinding.tvTaskListItemChecklistCompletionRatio.setVisibility(View.VISIBLE);

        String checklistRatioAsString = String.format(Locale.getDefault(), "%d/%d",
                checklistCompletionCounter.totalChecked,
                checklistCompletionCounter.totalChecklistItems);

        itemBinding.tvTaskListItemChecklistCompletionRatio.setText(checklistRatioAsString);

        anyIconsInRow = true;
      }
      else
      {
        itemBinding.ivTaskListItemChecklist.setVisibility(View.GONE);
        itemBinding.tvTaskListItemChecklistCompletionRatio.setVisibility(View.GONE);
      }
    }

    // Grab the due date if it exists, display the image view and date accordingly
    // (do not display completed due dates) set colors too
    if(dueDateModelList.size() > 0)
    {
      DueDateModel dueDateModel = dueDateModelList.get(position);
      if(dueDateModel != null && !dueDateModel.isCompleted())
      {
        itemBinding.ivTaskListItemDueDate.setVisibility(View.VISIBLE);
        itemBinding.tvTaskListItemDueDate.setVisibility(View.VISIBLE);

        initDueDateUi(itemBinding.ivTaskListItemDueDate.getContext(),
                new Date(dueDateModel.getDueDate()),
                itemBinding.ivTaskListItemDueDate,
                itemBinding.tvTaskListItemDueDate);

        anyIconsInRow = true;
      }
      else
      {
        itemBinding.ivTaskListItemDueDate.setVisibility(View.GONE);
        itemBinding.tvTaskListItemDueDate.setVisibility(View.GONE);
      }
    }

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

  String getTaskListId()
  {
    return taskListId;
  }

  int getTaskListAdapterPosition()
  {
    return taskListAdapterPosition;
  }

  List<TaskListCardModel> getTaskListCardModelList()
  {
    return taskListItemList;
  }

  List<DueDateModel> getDueDateModelList()
  {
    return dueDateModelList;
  }

  List<ChecklistCompletionCounter> getChecklistCompletionCounterList()
  {
    return checklistCompletionCounterList;
  }

  List<AttachmentInfo> getAttachmentInfoList()
  {
    return attachmentInfoList;
  }

  private void initDueDateUi(Context context,
                             Date date,
                             ImageView dueDateImageView,
                             TextView dueDateTextView)
  {
    // Build the string
    Calendar dueDateAsCalendarObject = Calendar.getInstance();
    dueDateAsCalendarObject.setTime(date);

    // Set up a comparison of the current time against the due date
    int compareDate = Calendar.getInstance().compareTo(dueDateAsCalendarObject);

    // Check the current time against the due date and set the icon color
    Drawable drawable;

    if(compareDate <= 0)
    {
      // Not due yet

      // determine how close we are to the due date
      Calendar calendar24HoursFromNow = Calendar.getInstance();
      calendar24HoursFromNow.add(Calendar.HOUR_OF_DAY, 24);

      int compareTime = calendar24HoursFromNow.compareTo(dueDateAsCalendarObject);

      if(compareTime <= 0)
      {
        // we have at least 24 hours left
        drawable = TaskMasterUtils.setDrawableResColorRes(context,
                R.drawable.ic_baseline_schedule_24px,
                R.color.due_date_soon);

        Format dateFormatter = new SimpleDateFormat(dueDateImageView.getResources().getString(R.string.date_time_format_abbreviated_month_name_day_string), Locale.getDefault());
        String dateFormatted = dateFormatter.format(dueDateAsCalendarObject.getTime());

        dueDateTextView.setText(dateFormatted);
        dueDateTextView.setTextColor(dueDateTextView.getResources().getColor(R.color.due_date_soon));
      }
      else
      {
        // we have less than 24 hours left
        drawable = TaskMasterUtils.setDrawableResColorRes(context,
                R.drawable.ic_baseline_schedule_24px,
                R.color.due_date_sooner);

        Format timeFormatter = new SimpleDateFormat(dueDateImageView.getResources().getString(R.string.date_time_format_hour_with_leading_zero_minute_period_string), Locale.getDefault());
        String timeFormatted = timeFormatter.format(dueDateAsCalendarObject.getTime());

        dueDateTextView.setText(timeFormatted);
        dueDateTextView.setTextColor(dueDateTextView.getResources().getColor(R.color.due_date_sooner));
      }
    }
    else
    {
      // past due

      // set the past due color
      drawable = TaskMasterUtils.setDrawableResColorRes(context,
              R.drawable.ic_baseline_schedule_24px,
              R.color.due_date_past);

      // See how far past the due we are, since we may need to add the year
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      int dueDateYear = dueDateAsCalendarObject.get(Calendar.YEAR);

      Format dateFormatter;
      String dateFormatted;
      if(currentYear == dueDateYear)
      {
        // current year, so no need to show the year
        dateFormatter = new SimpleDateFormat(dueDateImageView.getResources().getString(R.string.date_time_format_abbreviated_month_name_day_string), Locale.getDefault());
        dateFormatted = dateFormatter.format(dueDateAsCalendarObject.getTime());
      }
      else
      {
        // not the current year, so we need to show the year
        dateFormatter = new SimpleDateFormat(dueDateImageView.getResources().getString(R.string.date_time_format_abbreviated_month_name_day_year_string), Locale.getDefault());
        dateFormatted = dateFormatter.format(dueDateAsCalendarObject.getTime());
      }

      dueDateTextView.setText(dateFormatted);
      dueDateTextView.setTextColor(dueDateTextView.getResources().getColor(R.color.due_date_past));
    }

    dueDateImageView.setImageDrawable(drawable);
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

  private void moveCardDown(int i)
  {
    Collections.swap(taskListItemList, i, i + 1);
    Collections.swap(attachmentInfoList, i, i + 1);
    Collections.swap(checklistCompletionCounterList, i, i + 1);
    Collections.swap(dueDateModelList, i, i + 1);

    // update the card click listener positions
    taskListItemList.get(i).setCardIndex(i);
    taskListItemList.get(i + 1).setCardIndex(i + 1);
  }

  private void moveCardUp(int i)
  {
    Collections.swap(taskListItemList, i, i - 1);
    Collections.swap(attachmentInfoList, i, i - 1);
    Collections.swap(checklistCompletionCounterList, i, i - 1);
    Collections.swap(dueDateModelList, i, i - 1);

    taskListItemList.get(i).setCardIndex(i);
    taskListItemList.get(i - 1).setCardIndex(i - 1);
  }

  void onCardMovedInternal(int fromPosition, int toPosition)
  {
    if(fromPosition < toPosition)
    {
      for(int i = fromPosition; i < toPosition; ++i)
      {
        moveCardDown(i);
      }
    }
    else
    {
      for(int i = fromPosition; i > toPosition; --i)
      {
        moveCardUp(i);
      }
    }
    notifyItemMoved(fromPosition, toPosition);
  }

  void onCardMovedInternalAndUpdateDatabase(Context context, int fromPosition, int toPosition)
  {
    if(fromPosition < toPosition)
    {
      for(int i = fromPosition; i < toPosition; ++i)
      {
        // No need to move the card since it has already been moved during the drag location handler

        TaskListCardModel taskListCardModelOne = taskListItemList.get(i);
        TaskListCardModel taskListCardModelTwo = taskListItemList.get(i + 1);

        FirebaseRealtimeDbProvider.updateTaskListCardWithIndex(context,
                taskListCardModelOne.getCardId(),
                taskListCardModelOne.getCardIndex(),
                taskListCardModelOne.getTaskListId(),
                new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // update success
              Log.i("TaskListListAdp", "updated task list card index in database");
            }
            else
            {
              // update failure
              Log.i("TaskListListAdp", "failed to update task list card index in database");
            }
          }
        });

        FirebaseRealtimeDbProvider.updateTaskListCardWithIndex(context,
                taskListCardModelTwo.getCardId(),
                taskListCardModelTwo.getCardIndex(),
                taskListCardModelTwo.getTaskListId(),
                new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // update success
              Log.i("TaskListListAdp", "updated task list card index in database");
            }
            else
            {
              // update failure
              Log.i("TaskListListAdp", "failed to update task list card index in database");
            }
          }
        });
      }
    }
    else
    {
      for(int i = fromPosition; i > toPosition; --i)
      {
        // No need to move the card since it has already been moved during the drag location handler

        TaskListCardModel taskListCardModelOne = taskListItemList.get(i);
        TaskListCardModel taskListCardModelTwo = taskListItemList.get(i - 1);

        FirebaseRealtimeDbProvider.updateTaskListCardWithIndex(context,
                taskListCardModelOne.getCardId(),
                taskListCardModelOne.getCardIndex(),
                taskListCardModelOne.getTaskListId(),
                new DatabaseReference.CompletionListener()
                {
                  @Override
                  public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                  {
                    if(databaseError == null)
                    {
                      // update success
                      Log.i("TaskListListAdp", "updated task list card index in database");
                    }
                    else
                    {
                      // update failure
                      Log.i("TaskListListAdp", "failed to update task list card index in database");
                    }
                  }
                });

        FirebaseRealtimeDbProvider.updateTaskListCardWithIndex(context,
                taskListCardModelTwo.getCardId(),
                taskListCardModelTwo.getCardIndex(),
                taskListCardModelTwo.getTaskListId(),
                new DatabaseReference.CompletionListener()
                {
                  @Override
                  public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                  {
                    if(databaseError == null)
                    {
                      // update success
                      Log.i("TaskListListAdp", "updated task list card index in database");
                    }
                    else
                    {
                      // update failure
                      Log.i("TaskListListAdp", "failed to update task list card index in database");
                    }
                  }
                });
      }
    }

    // No need to notify this adapter since this has been done during the drag location handler
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
      Context context = view.getContext();

      if(TaskMasterUtils.isNetworkAvailable(context))
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
      }
      else
      {
        Toast.makeText(context,
                context.getString(R.string.error_network_not_available),
                Toast.LENGTH_LONG).show();
      }

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
