package com.example.android.taskmaster.view;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityMainBinding;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.dialog.CreateGroupDialogFragment;
import com.example.android.taskmaster.view.dialog.ICreateGroupDialogListener;
import com.example.android.taskmaster.view.widget.TaskMasterWidgetProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ITaskGroupListItemClickListener,
                                                               ICreateGroupDialogListener
{
  private ActivityMainBinding binding;
  private List<TaskGroupModel> taskGroupList;
  private TaskGroupListAdapter adapter;

  public static Intent getStartIntent(Context context)
  {
    Intent intent = new Intent(context, MainActivity.class);

    // Also clear the back stack
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            Intent.FLAG_ACTIVITY_CLEAR_TASK  |
            Intent.FLAG_ACTIVITY_NEW_TASK);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    if(savedInstanceState == null)
    {
      if(!TaskMasterUtils.isUserLoggedIn(this))
      {
        finish();

        Intent intent = WelcomeActivity.getStartIntent(this);
        startActivity(intent);
      }
      else
      {
        // we are logged in
        taskGroupList = new ArrayList<>();

        fetchRemoteData();

        postActivityLogInSetup();

        // Notify the widget that we are logged in
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskMasterWidgetProvider.class));
        TaskMasterWidgetProvider.updateTaskMasterWidgets(this, appWidgetManager, appWidgetIds);
      }
    }
    else
    {
      taskGroupList = savedInstanceState.getParcelableArrayList(getString(R.string.task_group_list_key));

      postActivityLogInSetup();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    outState.putParcelableArrayList(getString(R.string.task_group_list_key),
            new ArrayList<Parcelable>(taskGroupList));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.action_log_out:
      {
        logoutHandler();
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  @Override
  public void onTaskGroupListItemClick(int index)
  {
    Intent intent = TaskGroupActivity.getStartIntent(this,
            taskGroupList.get(index),
            taskGroupList);

    startActivity(intent);
  }

  @Override
  public void onCreateGroupClick(String newGroupName)
  {
    TaskGroupModel taskGroupModel = new TaskGroupModel(UUID.randomUUID().toString(),
            newGroupName,
            TaskMasterConstants.DEFAULT_BACKGROUND);

    taskGroupList.add(taskGroupModel);

    // The list must be displayed sorted by task name
    Collections.sort(taskGroupList, new SortByTaskName());

    // TODO: Add the new task group to the database too

    // Refresh the recycler view
    adapter.notifyDataSetChanged();

    refreshRecyclerViewVisibilityState();
  }

  private void registerClickHandler()
  {
    binding.fabMainActivityCreateGroup.setOnClickListener(new CreateGroupFabClickHandler());
  }

  private void setupRecyclerView()
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.rvMainActivity.setLayoutManager(layoutManager);

    adapter = new TaskGroupListAdapter(taskGroupList, this);
    binding.rvMainActivity.setAdapter(adapter);
  }

  private void refreshRecyclerViewVisibilityState()
  {
    if(taskGroupList.isEmpty())
    {
      binding.rvMainActivity.setVisibility(View.INVISIBLE);
      binding.tvErrorMainActivityNoTaskGroups.setVisibility(View.VISIBLE);
    }
    else
    {
      binding.tvErrorMainActivityNoTaskGroups.setVisibility(View.INVISIBLE);
      binding.rvMainActivity.setVisibility(View.VISIBLE);
    }
  }

  private void postActivityLogInSetup()
  {
    // set up the toolbar
    setSupportActionBar(binding.tbMainActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setTitle(getString(R.string.main_activity_title_string));
    }

    registerClickHandler();

    setupRecyclerView();

    refreshRecyclerViewVisibilityState();
  }

  private void fetchRemoteData()
  {
    // TODO: Grab the task groups from the database

    // TODO: Refresh the recycler view visibility state too
  }

  private void logoutHandler()
  {
    // TODO: Actually log out

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putBoolean(getString(R.string.shared_pref_user_logged_in_key), false);
    editor.apply();

    // Start the welcome activity since we are logged out
    finish();
    Intent intent = WelcomeActivity.getStartIntent(this);
    startActivity(intent);
  }

  class SortByTaskName implements Comparator<TaskGroupModel>
  {
    public int compare(TaskGroupModel a, TaskGroupModel b)
    {
      return a.getTitle().compareTo(b.getTitle());
    }
  }

  class CreateGroupFabClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Show the create group dialog
      CreateGroupDialogFragment.newInstance().show(getSupportFragmentManager());
    }
  }
}
