package com.example.android.taskmaster.view.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.CardDetailActivity;
import com.example.android.taskmaster.view.WelcomeActivity;

/**
 * Implementation of the TaskMaster Widget functionality.
 */
public class TaskMasterWidgetProvider extends AppWidgetProvider
{
  static void updateTaskMasterWidget(Context context,
                                     AppWidgetManager appWidgetManager,
                                     int appWidgetId)
  {
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_provider);

    // Check to see if we are logged in
    if(TaskMasterUtils.isUserLoggedIn(context))
    {
      // show the list container, hide the button
      views.setViewVisibility(R.id.gv_due_date_widget, View.VISIBLE);
      views.setViewVisibility(R.id.sign_in_widget, View.GONE);

      Intent intent = new Intent(context, TaskMasterGridWidgetService.class);
      views.setRemoteAdapter(R.id.gv_due_date_widget, intent);

      // Note: The RemoteViewsFactory needs to fill in the following
      //
      //       1) The Task Group Title
      //       2) The Task List Title
      //       3) The Card Model
      Intent cardDetailIntent = new Intent(context, CardDetailActivity.class);
      PendingIntent cardDetailPendingIntent = PendingIntent.getActivity(context,
              0,
              cardDetailIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);

      views.setPendingIntentTemplate(R.id.gv_due_date_widget, cardDetailPendingIntent);

      views.setEmptyView(R.id.gv_due_date_widget, R.id.empty_widget);
    }
    else
    {
      // show the button container, hide the list
      views.setViewVisibility(R.id.sign_in_widget, View.VISIBLE);
      views.setViewVisibility(R.id.gv_due_date_widget, View.GONE);

      // Set up the click handler for the sign in button
      Intent welcomeActivityIntent = WelcomeActivity.getStartIntent(context);
      PendingIntent welcomeActivityPendingIntent = PendingIntent.getActivity(context,
              0,
              welcomeActivityIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);

      views.setOnClickPendingIntent(R.id.button_log_in_widget, welcomeActivityPendingIntent);
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    // There may be multiple widgets active, so update all of them
    for(int appWidgetId : appWidgetIds)
    {
      TaskMasterWidgetProvider.updateTaskMasterWidget(context, appWidgetManager, appWidgetId);
    }
  }

  public static void updateTaskMasterWidgets(Context context,
                                             AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds)
  {
    for(int appWidgetId : appWidgetIds)
    {
      TaskMasterWidgetProvider.updateTaskMasterWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context)
  {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context)
  {
    // Enter relevant functionality for when the last widget is disabled
  }
}

