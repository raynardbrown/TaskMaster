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
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Intent.FLAG_ACTIVITY_CLEAR_TASK |
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

        // We must set up all the UI components before fetching data since the fetching mechanism
        // has dependencies on the initialized UI components.
        postActivityLogInSetup();

        fetchRemoteData();

        // Notify the widget that we are logged in
        notifyWidget();
      }
    }
    else
    {
      taskGroupList = savedInstanceState.getParcelableArrayList(getString(R.string.task_group_list_key));

      postActivityLogInSetup();
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    // We need to override this function to handle coming back to this activity from the child
    // activity.

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    if(sharedPreferences.getBoolean(getString(R.string.shared_pref_app_state_changed_key), false))
    {
      // Don't forget the clear the state
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(getString(R.string.shared_pref_app_state_changed_key), false);
      editor.apply();

      taskGroupList.clear();

      fetchRemoteData();
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
    switch(item.getItemId())
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

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    // table/object
    final String taskGroupPrimaryKey = taskGroupModel.getId();

    Map<String, Object> childUpdates = new HashMap<>();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    if(firebaseAuth.getCurrentUser() != null) // TODO: Handle null which means not logged in (Go back to the welcome activity?)
    {
      String currentUserEmailAddress = firebaseAuth.getCurrentUser().getEmail();

      // Handle null which means no email associated with the user.
      // This should never happen since if we are logged in, we have an email address.
      if(currentUserEmailAddress != null)
      {
        // Firebase does not allow a key to contain a period "."
        String firebaseSafeEmailKey = currentUserEmailAddress.replace(".", ",");

        String taskGroupModelRoot = String.format("/%s/%s/", getString(R.string.db_task_group_object), taskGroupPrimaryKey);

        String taskGroupModelUserRoot = String.format("/%s/%s/%s", getString(R.string.db_task_group_user), firebaseSafeEmailKey, taskGroupPrimaryKey);

        childUpdates.put(taskGroupModelRoot + getString(R.string.db_task_group_object_title),
                taskGroupModel.getTitle());

        childUpdates.put(taskGroupModelRoot + getString(R.string.db_task_group_object_color_key),
                taskGroupModel.getColorKey());

        // Link the task group to a user
        childUpdates.put(taskGroupModelUserRoot, true);

        rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // write success
              Log.i("MainAct", "wrote task group to database");
            }
            else
            {
              // write failure
              Log.i("MainAct", "failed to write task group to database");
            }
          }
        });

        // Refresh the recycler view
        adapter.notifyDataSetChanged();

        refreshRecyclerViewVisibilityState();
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
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    if(firebaseAuth.getCurrentUser() != null) // TODO: Handle null which means not logged in (Go back to the welcome activity?)
    {
      String currentUserEmailAddress = firebaseAuth.getCurrentUser().getEmail();

      // Handle null which means no email associated with the user.
      // This should never happen since if we are logged in, we have an email address.
      if(currentUserEmailAddress != null)
      {
        // Firebase does not allow a key to contain a period "."
        String firebaseSafeEmailKey = currentUserEmailAddress.replace(".", ",");

        // Grab all the task group id where the current user is a member
        databaseReference.child(getString(R.string.db_task_group_user)).child(firebaseSafeEmailKey)
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot)
                  {
                    for(DataSnapshot taskGroupUserSnapshot : dataSnapshot.getChildren())
                    {
                      // the value is a boolean which we do not care about
                      String taskGroupId = taskGroupUserSnapshot.getKey();

                      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                      // grab all the task groups associated with the specified task group id
                      databaseReference.child(getString(R.string.db_task_group_object)).child(taskGroupId)
                              .addListenerForSingleValueEvent(new TaskGroupValueEventListener(taskGroupId));
                    }
                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError)
                  {
                    Log.i("MainAct", "failed to get task group id from database");
                  }
                });
      }
    }
  }

  private void logoutHandler()
  {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    firebaseAuth.signOut();

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putBoolean(getString(R.string.shared_pref_user_logged_in_key), false);
    editor.apply();

    // Notify the widget that we are logged out
    notifyWidget();

    // Start the welcome activity since we are logged out
    finish();
    Intent intent = WelcomeActivity.getStartIntent(this);
    startActivity(intent);
  }

  private void notifyWidget()
  {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskMasterWidgetProvider.class));
    TaskMasterWidgetProvider.updateTaskMasterWidgets(this, appWidgetManager, appWidgetIds);
  }

  class SortByTaskName implements Comparator<TaskGroupModel>
  {
    public int compare(TaskGroupModel a, TaskGroupModel b)
    {
      return a.getTitle().compareTo(b.getTitle());
    }
  }

  class TaskGroupValueEventListener implements ValueEventListener
  {
    private String taskGroupId;

    TaskGroupValueEventListener(String taskGroupId)
    {
      this.taskGroupId = taskGroupId;
    }

    @Override
    public void onDataChange(DataSnapshot taskGroupSnapshot)
    {
      String taskGroupTitle = taskGroupSnapshot.child(getString(R.string.db_task_group_object_title)).getValue(String.class);

      // Must use Long.class and not String.class or you get the following exception
      // com.google.firebase.database.DatabaseException: Failed to convert value of type java.lang.Long to String
      //
      // Remember the type must match what you passed to the Map which was then passed to
      // DatabaseReference.updateChildren
      Long taskGroupColorKeyLong = taskGroupSnapshot.child(getString(R.string.db_task_group_object_color_key)).getValue(Long.class);

      int taskGroupColorKey = taskGroupColorKeyLong != null ? taskGroupColorKeyLong.intValue() : 0;

      taskGroupList.add(new TaskGroupModel(taskGroupId,
              taskGroupTitle,
              taskGroupColorKey));

      // The list must be displayed sorted by task name
      Collections.sort(taskGroupList, new SortByTaskName());

      adapter.notifyDataSetChanged();

      // Refresh the recycler view visibility state too
      refreshRecyclerViewVisibilityState();
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("MainAct", "failed to get task group from database");
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
