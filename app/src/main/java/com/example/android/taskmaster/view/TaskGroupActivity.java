package com.example.android.taskmaster.view;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityTaskGroupBinding;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.view.dialog.ChooseBackgroundDialogFragment;
import com.example.android.taskmaster.view.dialog.IAddCardDialogListener;
import com.example.android.taskmaster.view.dialog.IAddTaskListDialogListener;
import com.example.android.taskmaster.view.dialog.IChooseBackgroundDialogListener;
import com.example.android.taskmaster.view.dialog.IMoveTaskListDialogListener;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupActivity extends AppCompatActivity implements IChooseBackgroundDialogListener,
                                                                    IAddCardDialogListener,
                                                                    IAddTaskListDialogListener,
                                                                    IMoveTaskListDialogListener
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
        ChooseBackgroundDialogFragment dialogFragment = new ChooseBackgroundDialogFragment();

        Bundle bundle = new Bundle();
        // TODO: Pass the actual background color as a TaskMasterConstants.XXX_BACKGROUND
        bundle.putInt(getString(R.string.choose_background_color_key), 1);

        dialogFragment.setArguments(bundle);

        dialogFragment.show(getSupportFragmentManager(),
                getString(R.string.task_group_activity_dialog_choose_background_tag_string));
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

    adapter = new TaskListAdapter(this, taskListList);
    binding.rvTaskGroupActivity.setAdapter(adapter);
  }

  private void setStatusBarColorHelper(int color)
  {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(color);
    }
  }

  @Override
  public void onBackgroundSelected(int colorId)
  {
    ActionBar actionBar = getSupportActionBar();

    switch(colorId)
    {
      case TaskMasterConstants.RED_BACKGROUND:
      {
        if(actionBar != null)
        {
          setStatusBarColorHelper(getResources().getColor(R.color.task_dark_red));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_red)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_red));
        }
        break;
      }

      case TaskMasterConstants.GREEN_BACKGROUND:
      {
        if(actionBar != null)
        {
          setStatusBarColorHelper(getResources().getColor(R.color.task_dark_green));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_green)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_green));
        }
        break;
      }

      case TaskMasterConstants.INDIGO_BACKGROUND:
      {
        if(actionBar != null)
        {
          setStatusBarColorHelper(getResources().getColor(R.color.task_dark_indigo));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_indigo)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_indigo));
        }
        break;
      }
    }
  }

  @Override
  public void onAddCardClick(String cardTitle, String detailedDescription, int position)
  {
    // TODO: Get the task list from the list at the specified position

    // TODO: Add a new card to the task list

    // TODO: Notify the adapter
  }

  @Override
  public void onAddTaskListClick(String taskListName)
  {
    // TODO: Actually add the task list to the end of the list

    // TODO: Notify the adapter
  }

  @Override
  public void onTaskListMoveClick(String newTaskGroup)
  {
    // TODO: Actually remove the task from the database

    // TODO: Actually remove the task from the list

    // TODO: Notify the adapter
  }
}
