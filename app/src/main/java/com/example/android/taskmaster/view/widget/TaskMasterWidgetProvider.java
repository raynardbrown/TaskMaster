package com.example.android.taskmaster.view.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.CardDetailActivity;
import com.example.android.taskmaster.view.MainActivity;
import com.example.android.taskmaster.view.TaskGroupActivity;
import com.example.android.taskmaster.view.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the TaskMaster Widget functionality.
 */
public class TaskMasterWidgetProvider extends AppWidgetProvider
{
  static void updateTaskMasterWidget(Context context,
                                     AppWidgetManager appWidgetManager,
                                     int appWidgetId,
                                     List<TaskGroupModel> taskGroupModelList)
  {
    // Check to see if we are logged in
    if(TaskMasterUtils.isUserLoggedIn(context))
    {
      // Construct the RemoteViews object
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_provider);

      // show the list container, hide the button
      views.setViewVisibility(R.id.gv_due_date_widget, View.VISIBLE);
      views.setViewVisibility(R.id.sign_in_widget, View.GONE);

      Intent intent = new Intent(context, TaskMasterGridWidgetService.class);
      views.setRemoteAdapter(R.id.gv_due_date_widget, intent);

      // Note: The RemoteViewsFactory needs to fill in the following for card detail activity
      //
      //       1) The Task Group Title
      //       2) The Task List Title
      //       3) The Card Model

      // Put the back stack in
      Intent mainActivityIntent = new Intent(context, MainActivity.class);

      Intent taskGroupActivityIntent = new Intent(context, TaskGroupActivity.class);

      // the task group activity will get the task group model from shared preferences
      taskGroupActivityIntent.putExtra(context.getString(R.string.task_group_model_object_list_key), new ArrayList<>(taskGroupModelList));

      Intent cardDetailActivityIntent = new Intent(context, CardDetailActivity.class);

      PendingIntent pendingIntent = TaskStackBuilder.create(context)
              .addNextIntent(mainActivityIntent)
              .addNextIntent(taskGroupActivityIntent)
              .addNextIntent(cardDetailActivityIntent)
              .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

      views.setPendingIntentTemplate(R.id.gv_due_date_widget, pendingIntent);

      views.setEmptyView(R.id.gv_due_date_widget, R.id.empty_widget);

      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gv_due_date_widget);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    else
    {
      // Construct the RemoteViews object
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_provider);

      // show the button container, hide the list
      views.setViewVisibility(R.id.sign_in_widget, View.VISIBLE);
      views.setViewVisibility(R.id.gv_due_date_widget, View.GONE);
      views.setViewVisibility(R.id.empty_widget, View.GONE);

      // Set up the click handler for the sign in button
      Intent welcomeActivityIntent = WelcomeActivity.getStartIntent(context);
      PendingIntent welcomeActivityPendingIntent = PendingIntent.getActivity(context,
              0,
              welcomeActivityIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);

      views.setOnClickPendingIntent(R.id.button_log_in_widget, welcomeActivityPendingIntent);

      // I'm not sure if this is expected behavior or an Android workaround. If I do not include
      // this line and a user logs out of the task master app (NOTE: Task master clears the
      // collection view's data store) then the empty_widget is shown instead of the sign_in_widget
      // even though I've set both the gv_due_date_widget and the empty_widget GONE and made the
      // sign_in_widget VISIBLE.
      //
      // It looks like once you call setEmptyView on your collection view, then the calls to
      // setViewVisibility appear to be ignored.
      //
      // The workaround seems to be setting sign_in_widget as an empty view to the collection view.
      // Then once the user logs out of the app the sign in widget is displayed.
      views.setEmptyView(R.id.gv_due_date_widget, R.id.sign_in_widget);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  static void updateTaskMasterWidget(Context context,
                                     AppWidgetManager appWidgetManager,
                                     int appWidgetId)
  {
    // Check to see if we are logged in
    if(TaskMasterUtils.isUserLoggedIn(context))
    {
      // Construct the RemoteViews object
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_provider);

      // show the list container, hide the button
      views.setViewVisibility(R.id.gv_due_date_widget, View.VISIBLE);
      views.setViewVisibility(R.id.sign_in_widget, View.GONE);

      Intent intent = new Intent(context, TaskMasterGridWidgetService.class);
      views.setRemoteAdapter(R.id.gv_due_date_widget, intent);

      views.setEmptyView(R.id.gv_due_date_widget, R.id.empty_widget);

      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gv_due_date_widget);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    else
    {
      // Construct the RemoteViews object
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_provider);

      // show the button container, hide the list
      views.setViewVisibility(R.id.sign_in_widget, View.VISIBLE);
      views.setViewVisibility(R.id.gv_due_date_widget, View.GONE);
      views.setViewVisibility(R.id.empty_widget, View.GONE);

      // Set up the click handler for the sign in button
      Intent welcomeActivityIntent = WelcomeActivity.getStartIntent(context);
      PendingIntent welcomeActivityPendingIntent = PendingIntent.getActivity(context,
              0,
              welcomeActivityIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);

      views.setOnClickPendingIntent(R.id.button_log_in_widget, welcomeActivityPendingIntent);

      // I'm not sure if this is expected behavior or an Android workaround. If I do not include
      // this line and a user logs out of the task master app (NOTE: Task master clears the
      // collection view's data store) then the empty_widget is shown instead of the sign_in_widget
      // even though I've set both the gv_due_date_widget and the empty_widget GONE and made the
      // sign_in_widget VISIBLE.
      //
      // It looks like once you call setEmptyView on your collection view, then the calls to
      // setViewVisibility appear to be ignored.
      //
      // The workaround seems to be setting sign_in_widget as an empty view to the collection view.
      // Then once the user logs out of the app the sign in widget is displayed.
      views.setEmptyView(R.id.gv_due_date_widget, R.id.sign_in_widget);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    // There may be multiple widgets active, so update all of them
    for(int appWidgetId : appWidgetIds)
    {
      TaskMasterWidgetProvider.updateTaskMasterWidget(context,
              appWidgetManager,
              appWidgetId);
    }

    TaskMasterWidgetProvider.startDueDateFetchService(context);
  }

  public static void updateTaskMasterWidgets(Context context,
                                             AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds,
                                             List<TaskGroupModel> taskGroupModelList)
  {
    for(int appWidgetId : appWidgetIds)
    {
      TaskMasterWidgetProvider.updateTaskMasterWidget(context, appWidgetManager, appWidgetId, taskGroupModelList);
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

  public static void startDueDateFetchService(Context context)
  {
    // start the due date fetch
    Intent intent = DueDateFetchIntentService.getStartIntent(context);

    context.startService(intent);
  }

  public static void startDeleteDueDateService(Context context, String taskListCardId)
  {
    Intent intent = DueDateDeleteIntentService.getStartIntent(context, taskListCardId);

    context.startService(intent);
  }

  public static void startDeleteAllDueDateService(Context context)
  {
    Intent intent = DueDateDeleteAllIntentService.getStartIntent(context);

    context.startService(intent);
  }

  public static void startUpdateDueDateService(Context context,
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
    Intent intent = DueDateUpdateIntentService.getStartIntent(context,
            taskListCardId,
            taskListCardTitle,
            taskListCardDetailed,
            taskListCardIndex,
            taskListIndex,
            taskListId,
            taskListTitle,
            taskGroupId,
            taskGroupTitle,
            taskGroupColorKey,
            dueDateModel,
            taskGroupModelList);

    context.startService(intent);
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

  @Override
  public void onReceive(Context context, Intent intent)
  {
    super.onReceive(context, intent);

    String action = intent.getAction();

    if(action != null)
    {
      if(action.equals(DueDateFetchIntentService.ACTION_DUE_DATE_RESP_WITH_DATA) ||
              action.equals(DueDateUpdateIntentService.ACTION_DUE_DATE_UPDATED_RESP))
      {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TaskMasterWidgetProvider.class));

        List<TaskGroupModel> taskGroupModelList = intent.getParcelableArrayListExtra(context.getString(R.string.task_group_model_object_list_key));

        TaskMasterWidgetProvider.updateTaskMasterWidgets(context,
                appWidgetManager,
                appWidgetIds,
                taskGroupModelList);
      }
      else if((action.equals(DueDateFetchIntentService.ACTION_DUE_DATE_RESP_NO_DATA) ||
              action.equals(DueDateDeleteIntentService.ACTION_DUE_DATE_DELETED_RESP) ||
              action.equals(DueDateDeleteAllIntentService.ACTION_DUE_DATE_DELETE_ALL_RESP)))
      {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TaskMasterWidgetProvider.class));

        TaskMasterWidgetProvider.updateTaskMasterWidgets(context, appWidgetManager, appWidgetIds);
      }
    }
  }
}

