package com.example.android.taskmaster.view.widget;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.util.ArrayList;
import java.util.List;

public class DueDateUpdateIntentService extends IntentService
{
  public static final String ACTION_DUE_DATE_UPDATED_RESP = "com.example.android.taskmaster.action.DUE_DATE_UPDATED";

  public static Intent getStartIntent(Context context,
                                      String taskListCardId,
                                      String taskListCardTitle,
                                      String taskListCardDetailed,
                                      int taskListCardIndex,
                                      int taskListIndex,
                                      String taskListId,
                                      String taskListTitle,
                                      String taskGroupId,
                                      String taskGroupTitle,
                                      int taskGroupColorKey,
                                      DueDateModel dueDateModel,
                                      List<TaskGroupModel> taskGroupModelList)
  {
    Intent intent = new Intent(context, DueDateUpdateIntentService.class);

    intent.putExtra(context.getString(R.string.task_list_card_id_key), taskListCardId);
    intent.putExtra(context.getString(R.string.task_list_card_title_key), taskListCardTitle);
    intent.putExtra(context.getString(R.string.task_list_card_detailed_key), taskListCardDetailed);
    intent.putExtra(context.getString(R.string.task_list_card_index_key), taskListCardIndex);
    intent.putExtra(context.getString(R.string.task_list_index_key), taskListIndex);
    intent.putExtra(context.getString(R.string.task_list_id_key), taskListId);
    intent.putExtra(context.getString(R.string.task_list_title_key), taskListTitle);
    intent.putExtra(context.getString(R.string.task_group_id_key), taskGroupId);
    intent.putExtra(context.getString(R.string.task_group_title_key), taskGroupTitle);
    intent.putExtra(context.getString(R.string.task_group_color_key), taskGroupColorKey);
    intent.putExtra(context.getString(R.string.due_date_model_object_key), dueDateModel);
    intent.putExtra(context.getString(R.string.task_group_model_object_list_key), new ArrayList<>(taskGroupModelList));

    return intent;
  }

  public DueDateUpdateIntentService()
  {
    super("DueDateUpdateIntentService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent)
  {
    if(intent != null)
    {
      String taskListCardId = intent.getStringExtra(getString(R.string.task_list_card_id_key));
      String taskListCardTitle = intent.getStringExtra(getString(R.string.task_list_card_title_key));
      String taskListCardDetailed = intent.getStringExtra(getString(R.string.task_list_card_detailed_key));
      int taskListCardIndex = intent.getIntExtra(getString(R.string.task_list_card_index_key), 0);
      int taskListIndex = intent.getIntExtra(getString(R.string.task_list_index_key), 0);
      String taskListId = intent.getStringExtra(getString(R.string.task_list_id_key));
      String taskListTitle = intent.getStringExtra(getString(R.string.task_list_title_key));
      String taskGroupId = intent.getStringExtra(getString(R.string.task_group_id_key));
      String taskGroupTitle = intent.getStringExtra(getString(R.string.task_group_title_key));
      int taskGroupColorKey = intent.getIntExtra(getString(R.string.task_group_color_key), 0);
      DueDateModel dueDateModel = intent.getParcelableExtra(getString(R.string.due_date_model_object_key));

      List<TaskGroupModel> taskGroupModelList = intent.getParcelableArrayListExtra(getString(R.string.task_group_model_object_list_key));

      ContentValues contentValues = new ContentValues();

      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_ID, taskListCardId);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_TITLE, taskListCardTitle);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_DETAILED, taskListCardDetailed);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_INDEX, taskListCardIndex);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_INDEX, taskListIndex);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_ID, taskListId);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_TITLE, taskListTitle);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_ID, taskGroupId);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_TITLE, taskGroupTitle);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_COLOR_KEY, taskGroupColorKey);
      contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.DUE_DATE, dueDateModel.getDueDate());

      getContentResolver().insert(DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI, contentValues);

      // Notify the widget
      TaskMasterUtils.notifyWidget(DueDateUpdateIntentService.this,
              taskGroupModelList,
              DueDateUpdateIntentService.ACTION_DUE_DATE_UPDATED_RESP);
    }
  }
}
