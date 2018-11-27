package com.example.android.taskmaster.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityTaskGroupBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupActivity extends AppCompatActivity
{
  private ActivityTaskGroupBinding binding;

  private List<String> taskListList;

  private TaskListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_task_group);

    // set up the toolbar
    setSupportActionBar(binding.tbTaskGroupActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);
      // TODO: Put the actual title of the task group as sent from the intent
      actionBar.setTitle("Task Group XXX");
    }

    setupRecyclerView();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_task_group_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.action_background_color:
      {
        // TODO: Handle click
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private void setupRecyclerView()
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    binding.rvTaskGroupActivity.setLayoutManager(layoutManager);
    taskListList = new ArrayList<>();

    // TODO: Dummy data delete this
    taskListList.add("Task List One");
    taskListList.add("Task List Two");
    taskListList.add("Task List Three");

    adapter = new TaskListAdapter(taskListList);
    binding.rvTaskGroupActivity.setAdapter(adapter);
  }
}
