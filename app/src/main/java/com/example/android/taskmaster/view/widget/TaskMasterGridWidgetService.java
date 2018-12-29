package com.example.android.taskmaster.view.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskMasterGridWidgetService extends RemoteViewsService
{
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent)
  {
    return new TaskMasterGridWidgetRemoteViewsFactory(getApplicationContext());
  }
}
