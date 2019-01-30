package com.example.android.taskmaster.view.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.model.TaskListCardModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.util.Date;

public class TaskMasterGridWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
  private Context context;
  private Cursor cursor;

  TaskMasterGridWidgetRemoteViewsFactory(Context applicationContext)
  {
    this.context = applicationContext;
  }

  @Override
  public void onCreate()
  {
    // we do nothing here, we defer to onDataSetChanged to initialize the cursor
  }

  @Override
  public void onDataSetChanged()
  {
    if(cursor != null)
    {
      cursor.close();
    }

    Uri dueDateUri = DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI;

    // sort the data
    cursor = context.getContentResolver().query(dueDateUri,
            null,
            null,
            null,
            DueDateWidgetContract.DueDateWidgetColumns.DUE_DATE);
  }

  @Override
  public void onDestroy()
  {
    if(cursor != null)
    {
      cursor.close();
    }
  }

  @Override
  public int getCount()
  {
    if(cursor == null)
    {
      return 0;
    }
    else
    {
      return cursor.getCount();
    }
  }

  @Override
  public RemoteViews getViewAt(int position)
  {
    if(cursor != null)
    {
      if(cursor.moveToPosition(position))
      {
        String taskListCardId = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_ID));
        String taskListCardTitle = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_TITLE));
        String detailedDescription = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_DETAILED));
        int cardIndex = cursor.getInt(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_INDEX));
        int taskIndex = cursor.getInt(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_INDEX));
        String taskGroupId = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_ID));
        String taskGroupTitle = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_TITLE));
        int taskGroupColorKey = cursor.getInt(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_COLOR_KEY));
        String taskListId = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_ID));
        String taskListTitle = cursor.getString(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_TITLE));
        long dueDate = cursor.getLong(cursor.getColumnIndex(DueDateWidgetContract.DueDateWidgetColumns.DUE_DATE));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_due_date_item);
        views.setTextViewText(R.id.tv_widget_due_date_title_item_name, taskListCardTitle);

        // Set the color of the due date icon

        // Note: Widget are limited in how much memory they can use. We cannot use the setImageViewBitmap
        //       to tweak the image color because we will run out of memory. Furthermore, Android
        //       does not a provide a way to set tint of an image view using a remote view. At least
        //       I have not found out how to do this.
        //
        //       I have resorted to copying the image resource file and changing the color in each
        //       file to achieve the same effect. :-(
        int imageResId = TaskMasterUtils.getImageResIdForDueDate(context, new Date(dueDate));
        views.setImageViewResource(R.id.iv_widget_due_date_icon, imageResId);

        TaskGroupModel taskGroupModel = new TaskGroupModel(taskGroupId,
                taskGroupTitle,
                taskGroupColorKey);

        TaskListCardModel taskListCardModel = new TaskListCardModel(taskGroupId,
                taskListId,
                taskListCardId,
                taskIndex,
                taskListCardTitle,
                detailedDescription,
                cardIndex);

        Intent cardDetailActivityFillInIntent = new Intent();

        cardDetailActivityFillInIntent.putExtra(context.getString(R.string.task_group_model_object_key), taskGroupModel);
        cardDetailActivityFillInIntent.putExtra(context.getString(R.string.task_list_title_key), taskListTitle);
        cardDetailActivityFillInIntent.putExtra(context.getString(R.string.task_list_card_model_object_key), taskListCardModel);

        cardDetailActivityFillInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // launch the card detail activity when this item is clicked
        views.setOnClickFillInIntent(R.id.tv_widget_due_date_title_item_name, cardDetailActivityFillInIntent);

        return views;
      }
    }

    return null;
  }

  @Override
  public RemoteViews getLoadingView()
  {
    return null;
  }

  @Override
  public int getViewTypeCount()
  {
    return 1;
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

  @Override
  public boolean hasStableIds()
  {
    return true;
  }
}
