package com.example.android.taskmaster.view.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

public class DueDateDeleteAllIntentService extends IntentService
{
  public static final String ACTION_DUE_DATE_DELETE_ALL_RESP = "com.example.android.taskmaster.action.DUE_DATE_DELETE_ALL";

  public static Intent getStartIntent(Context context)
  {
    return new Intent(context, DueDateDeleteAllIntentService.class);
  }

  public DueDateDeleteAllIntentService()
  {
    super("DueDateDeleteAllIntentService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent)
  {
    if(intent != null)
    {
      // delete all rows
      getContentResolver().delete(DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI, null, null);

      // Notify the widget
      Intent broadcastIntent = new Intent(getApplicationContext(), TaskMasterWidgetProvider.class);
      broadcastIntent.setAction(DueDateDeleteAllIntentService.ACTION_DUE_DATE_DELETE_ALL_RESP);
      sendBroadcast(broadcastIntent);
    }
  }
}
