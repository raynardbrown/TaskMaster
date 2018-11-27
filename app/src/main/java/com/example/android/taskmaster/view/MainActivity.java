package com.example.android.taskmaster.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import com.example.android.taskmaster.view.dialog.CreateGroupDialogFragment;
import com.example.android.taskmaster.view.dialog.ICreateGroupDialogListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ITaskGroupListItemClickListener,
                                                               ICreateGroupDialogListener
{
  private ActivityMainBinding binding;
  private List<String> taskGroupList;
  private TaskGroupListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    // set up the toolbar
    setSupportActionBar(binding.tbMainActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setTitle(getString(R.string.main_activity_title_string));
    }

    registerClickHandler();
    setupRecyclerView();
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
        // TODO: Handle click
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private void registerClickHandler()
  {
    binding.fabMainActivityCreateGroup.setOnClickListener(new CreateGroupFabClickHandler());
  }

  private void setupRecyclerView()
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.rvMainActivity.setLayoutManager(layoutManager);
    taskGroupList = new ArrayList<>();

    // TODO: Dummy data delete this
    taskGroupList.add("Task Group One");
    taskGroupList.add("Task Group Two");
    taskGroupList.add("Task Group Three");
    taskGroupList.add("Task Group Four");

    adapter = new TaskGroupListAdapter(taskGroupList, this);
    binding.rvMainActivity.setAdapter(adapter);
  }

  @Override
  public void onTaskGroupListItemClick(int index)
  {
    // TODO: launch the task group activity for testing, we really need to use the database to
    //       pass extra data via the intent
    Intent intent = new Intent(this, TaskGroupActivity.class);

    startActivity(intent);
  }

  @Override
  public void onCreateGroupClick(String newGroupName)
  {
    // TODO: Actually create the group
  }

  class CreateGroupFabClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Show the create group dialog
      CreateGroupDialogFragment dialogFragment = new CreateGroupDialogFragment();
      dialogFragment.show(getSupportFragmentManager(),
              getString(R.string.main_activity_dialog_create_group_tag_string));
    }
  }
}
