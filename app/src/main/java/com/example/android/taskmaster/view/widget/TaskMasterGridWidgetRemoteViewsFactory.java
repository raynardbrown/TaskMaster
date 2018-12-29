package com.example.android.taskmaster.view.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskListCardModel;

import java.util.ArrayList;
import java.util.List;

public class TaskMasterGridWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
  private Context context;

  private List<DueDateModel> dueDateModelListDummy;

  public TaskMasterGridWidgetRemoteViewsFactory(Context applicationContext)
  {
    this.context = applicationContext;
  }

  @Override
  public void onCreate()
  {
    dueDateModelListDummy = new ArrayList<>();

    dueDateModelListDummy.add(new DueDateModel("123", 123, false));
    dueDateModelListDummy.add(new DueDateModel("1234", 256, false));
  }

  @Override
  public void onDataSetChanged()
  {

  }

  @Override
  public void onDestroy()
  {

  }

  @Override
  public int getCount()
  {
    return dueDateModelListDummy.size();
  }

  @Override
  public RemoteViews getViewAt(int position)
  {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_master_widget_due_date_item);

    views.setTextViewText(R.id.tv_widget_due_date_title_item_name, "First item");
    views.setTextViewText(R.id.tv_widget_due_date_title_item_name, "Second item");

    // TODO: Set the pending intent extra to launch the card detail activity
    Bundle bundle = new Bundle();
    bundle.putString(context.getString(R.string.task_group_title_key), "Task Group One");
    bundle.putString(context.getString(R.string.task_list_title_key), "Task List One");
    bundle.putParcelable(context.getString(R.string.task_list_card_model_object_key), new TaskListCardModel("1", "2", "3", 1, "A New Card", "A new card description", 1));

    Intent intent = new Intent();
    intent.putExtras(bundle);

    views.setOnClickFillInIntent(R.id.tv_widget_due_date_title_item_name, intent);

    return views;
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
