package com.example.android.taskmaster.view.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.taskmaster.R;

public class DueDateDeleteIntentService extends IntentService
{
  public static final String ACTION_DUE_DATE_DELETED_RESP = "com.example.android.taskmaster.action.DUE_DATE_DELETED";

  public static Intent getStartIntent(Context context, String taskListCardId)
  {
    Intent intent = new Intent(context, DueDateDeleteIntentService.class);

    intent.putExtra(context.getString(R.string.task_list_card_id_key), taskListCardId);

    return intent;
  }

  public DueDateDeleteIntentService()
  {
    super("DueDateDeleteIntentService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent)
  {
    if(intent != null)
    {
      String taskListCardId = intent.getStringExtra(getString(R.string.task_list_card_id_key));

      Uri uri = DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI;

      uri = uri.buildUpon().appendPath(taskListCardId).build();

      getContentResolver().delete(uri, null, null);

      // Notify the widget
      Intent broadcastIntent = new Intent(getApplicationContext(), TaskMasterWidgetProvider.class);
      broadcastIntent.setAction(DueDateDeleteIntentService.ACTION_DUE_DATE_DELETED_RESP);
      sendBroadcast(broadcastIntent);
    }
  }
}
