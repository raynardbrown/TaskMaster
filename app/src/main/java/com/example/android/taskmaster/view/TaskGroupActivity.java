package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityTaskGroupBinding;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.model.TaskListCardModel;
import com.example.android.taskmaster.model.TaskListModel;
import com.example.android.taskmaster.model.TaskListModelContainer;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.dialog.ChooseBackgroundDialogFragment;
import com.example.android.taskmaster.view.dialog.IAddCardDialogListener;
import com.example.android.taskmaster.view.dialog.IAddTaskListDialogListener;
import com.example.android.taskmaster.view.dialog.IChooseBackgroundDialogListener;
import com.example.android.taskmaster.view.dialog.IMoveTaskListDialogListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskGroupActivity extends AppCompatActivity implements IChooseBackgroundDialogListener,
                                                                    IAddTaskListDialogListener,
                                                                    IMoveTaskListDialogListener,
                                                                    IAddCardDialogListener,
                                                                    ITaskListCardClickDelegate
{
  private ActivityTaskGroupBinding binding;

  private List<TaskListModelContainer> taskListList;

  private TaskListAdapter adapter;

  private TaskGroupModel taskGroupModel;

  private List<TaskGroupModel> taskGroupModelList;

  public static Intent getStartIntent(Context context,
                                      TaskGroupModel taskGroupModel,
                                      List<TaskGroupModel> taskGroupModelList)
  {
    Intent intent = new Intent(context, TaskGroupActivity.class);
    intent.putExtra(context.getString(R.string.task_group_model_object_key), taskGroupModel);
    intent.putExtra(context.getString(R.string.task_group_model_object_list_key), new ArrayList<>(taskGroupModelList));
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_task_group);

    if(savedInstanceState == null)
    {
      Intent intent = getIntent();

      taskGroupModel = intent.getParcelableExtra(getString(R.string.task_group_model_object_key));

      taskGroupModelList = intent.getParcelableArrayListExtra(getString(R.string.task_group_model_object_list_key));

      taskListList = new ArrayList<>();

      postUiInitialization();

      fetchRemoteData();
    }
    else
    {
      taskGroupModel = savedInstanceState.getParcelable(getString(R.string.task_group_model_object_key));

      taskGroupModelList = savedInstanceState.getParcelableArrayList(getString(R.string.task_group_model_object_list_key));

      taskListList = savedInstanceState.getParcelableArrayList(getString(R.string.task_list_model_container_object_list_key));

      postUiInitialization();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    outState.putParcelable(getString(R.string.task_group_model_object_key), taskGroupModel);

    outState.putParcelableArrayList(getString(R.string.task_group_model_object_list_key),
            new ArrayList<>(taskGroupModelList));

    outState.putParcelableArrayList(getString(R.string.task_list_model_container_object_list_key),
            new ArrayList<Parcelable>(taskListList));
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
    switch(item.getItemId())
    {
      case R.id.action_background_color:
      {
        ChooseBackgroundDialogFragment dialogFragment = new ChooseBackgroundDialogFragment();

        Bundle bundle = new Bundle();

        bundle.putInt(getString(R.string.choose_background_color_key), taskGroupModel.getColorKey());

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

  @Override
  public void onBackgroundSelected(int colorId)
  {
    setActivityColorTheme(colorId);

    // Update the task group
    taskGroupModel.setColorKey(colorId);

    // Update the database

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    final String taskGroupPrimaryKey = taskGroupModel.getId();

    String taskGroupModelRoot = String.format("/%s/%s/", getString(R.string.db_task_group_object), taskGroupPrimaryKey);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskGroupModelRoot + getString(R.string.db_task_group_object_color_key),
            taskGroupModel.getColorKey());

    rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
    {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
      {
        if(databaseError == null)
        {
          // write success
          Log.i("TaskGroupAct", "updated task group color in database");

          SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TaskGroupActivity.this);
          SharedPreferences.Editor editor = sharedPreferences.edit();

          editor.putBoolean(getString(R.string.shared_pref_app_state_changed_key), true);
          editor.apply();
        }
        else
        {
          // write failure
          Log.i("TaskGroupAct", "failed to update task group color in database");
        }
      }
    });
  }

  @Override
  public void onAddCardClick(String cardTitle, String detailedDescription, int position)
  {
    // forward the click to the task list adapter
    adapter.onAddCardClick(cardTitle, detailedDescription, position);
  }

  @Override
  public void onAddTaskListClick(String taskListName)
  {
    TaskListModel taskListModel = new TaskListModel(taskGroupModel.getId(),
            UUID.randomUUID().toString(),
            taskListList.size(),
            taskListName);

    TaskListModelContainer taskListModelContainer = new TaskListModelContainer(taskListModel,
            new ArrayList<TaskListCardModel>(),
            new ArrayList<DueDateModel>());

    taskListList.add(taskListModelContainer);

    // Update the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListModelRoot = String.format("/%s/%s/", getString(R.string.db_task_list_object),
            taskListModelContainer.getTaskListModel().getTaskListId());

    String taskGroupTaskListRoot = String.format("/%s/%s/", getString(R.string.db_task_group_task_list_key), taskGroupModel.getId());

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListModelRoot + getString(R.string.db_task_list_index_key),
            taskListList.size() - 1);

    childUpdates.put(taskListModelRoot + getString(R.string.db_task_list_title_key),
            taskListModel.getTitle());

    // add the task list to the collection of task lists in the task_group_task_list
    childUpdates.put(taskGroupTaskListRoot + taskListModel.getTaskListId(),
            taskListModel.getTaskIndex());

    rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
    {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
      {
        if(databaseError == null)
        {
          // write success
          Log.i("TaskGroupAct", "wrote task list to database");
        }
        else
        {
          // write failure
          Log.i("TaskGroupAct", "failed to write task list to database");
        }
      }
    });

    // Notify the adapter
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onTaskListMoveClick(int taskListIndex,
                                  int currentTaskGroupIndex,
                                  int newTaskGroupIndex)
  {
    if(currentTaskGroupIndex != newTaskGroupIndex)
    {
      // remove the task list from the list, change indexes too
      final TaskListModelContainer taskListModelToMove = taskListList.remove(taskListIndex);

      final String destinationTaskGroupId = taskGroupModelList.get(newTaskGroupIndex).getId();

      for(int i = taskListIndex; i < taskListList.size(); ++i)
      {
        TaskListModel taskListModel = taskListList.get(i).getTaskListModel();
        taskListModel.setTaskIndex(i);

        // update the database for each changed entry (task_list and task_group_task_list)
        updateTaskListWithTaskIndex(taskListModel.getTaskListId(),
                taskListModel.getTaskIndex(),
                taskGroupModel.getId(),
                new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // write success
              Log.i("TaskGroupAct", "updated the task list index in the database");
            }
            else
            {
              // write failure
              Log.i("TaskGroupAct", "failed to update the task list index in the database");
            }
          }
        });

        // update the task index for all of your child cards
        TaskListModelContainer taskListModelContainer = taskListList.get(i);
        List<TaskListCardModel> taskListCardModelList = taskListModelContainer.getCardList();
        for(int j = 0; j < taskListCardModelList.size(); ++j)
        {
          TaskListCardModel taskListCardModel = taskListCardModelList.get(j);

          taskListCardModel.setTaskIndex(taskListModelContainer.getTaskListModel().getTaskIndex());

          // update the database too

          updateTaskListCardWithTaskIndex(taskListCardModel.getCardId(),
                  taskListCardModel.getTaskIndex(),
                  new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // write success
                Log.i("TaskGroupAct", "updated the task list index of the task list card in the database");
              }
              else
              {
                // write failure
                Log.i("TaskGroupAct", "failed to update the task list index of the task list card in the database");
              }
            }
          });
        }
      }

      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference rootDatabaseReference = database.getReference();
      //
      // Perform the task list move within the database, change indexes too
      //
      // 1) Get all of the indexes of the task lists from the destination task group
      //   (task_group_task_list)
      //
      // 2) Set the index of the removed task list to be the index after the last element
      //    in the destination task group (task_list).
      //
      // 3) Add the task list that we removed from this group to the destination group
      //   (task_group_task_list) while also removing it from the source task group
      //   (task_group_task_list)
      //
      // 4) Finally update the task list index of all the child cards of the moved task list
      //
      rootDatabaseReference.child(getString(R.string.db_task_group_task_list_key)).child(destinationTaskGroupId)
              .orderByValue()
              .limitToLast(1)
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  // we need to handle the case where we move a task list to a task group that has
                  // no existing task lists. In that case, the last task index is 0.

                  int newTaskIndex = 0;
                  if(dataSnapshot.hasChildren())
                  {
                    DataSnapshot taskListSnapShot = dataSnapshot.getChildren().iterator().next();

                    Long lastTaskIndexLong = taskListSnapShot.getValue(Long.class);

                    int lastTaskIndex = lastTaskIndexLong != null ? lastTaskIndexLong.intValue() : 0;

                    newTaskIndex = lastTaskIndex + 1;
                  }

                  final TaskListModel taskListModel = taskListModelToMove.getTaskListModel();

                  taskListModel.setTaskIndex(newTaskIndex);

                  updateTaskListWithTaskIndex(taskListModel.getTaskListId(),
                          taskListModel.getTaskIndex(),
                          destinationTaskGroupId,
                          new DatabaseReference.CompletionListener()
                  {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference)
                    {
                      if(databaseError == null)
                      {
                        // write success
                        Log.i("TaskGroupAct", "updated the task list index of the moved task list in the database");

                        addTaskListToTaskGroup(taskListModel.getTaskListId(),
                                taskListModel.getTaskIndex(),
                                destinationTaskGroupId,
                                new DatabaseReference.CompletionListener()
                        {
                          @Override
                          public void onComplete(DatabaseError databaseError,
                                                 DatabaseReference databaseReference)
                          {
                            if(databaseError == null)
                            {
                              // write success
                              Log.i("TaskGroupAct", "added the moved task list to the database");

                              removeTaskListFromTaskGroup(taskListModel.getTaskListId(),
                                      taskGroupModel.getId(),
                                      new DatabaseReference.CompletionListener()
                              {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference)
                                {
                                  if(databaseError == null)
                                  {
                                    // remove success
                                    Log.i("TaskGroupAct", "removed the moved task list from the old position in the database");

                                    List<TaskListCardModel> taskListCardModelList = taskListModelToMove.getCardList();
                                    for(int i = 0; i < taskListModelToMove.getCardList().size(); ++i)
                                    {
                                      TaskListCardModel taskListCardModel = taskListCardModelList.get(i);

                                      taskListCardModel.setTaskIndex(taskListModel.getTaskIndex());

                                      // update the database too
                                      updateTaskListCardWithTaskIndex(taskListCardModel.getCardId(),
                                              taskListCardModel.getTaskIndex(),
                                              new DatabaseReference.CompletionListener()
                                      {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                        {
                                          if(databaseError == null)
                                          {
                                            // write success
                                            Log.i("TaskGroupAct", "updated the task list index of the task list card in the database");
                                          }
                                          else
                                          {
                                            // write failure
                                            Log.i("TaskGroupAct", "failed to update the task list index of the task list card in the database");
                                          }
                                        }
                                      });
                                    }
                                  }
                                  else
                                  {
                                    // remove failure
                                    Log.i("TaskGroupAct", "failed to remove the moved task list from the old position in the database");
                                  }
                                }
                              });
                            }
                            else
                            {
                              // write failure
                              Log.i("TaskGroupAct", "failed to add the moved task list to the database");
                            }
                          }
                        });
                      }
                      else
                      {
                        // write failure
                        Log.i("TaskGroupAct", "failed to update the task list index of the moved task list in the database");
                      }
                    }
                  });
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                  Log.i("TaskGroupAct", "failed to get task list id from database");
                }
              });

      // Notify the adapter
      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onTaskListCardClick(String taskListTitle,TaskListCardModel taskListCardModel)
  {
    Intent intent = CardDetailActivity.getStartIntent(this,
            taskGroupModel.getTitle(),
            taskListTitle,
            taskListCardModel);

    startActivity(intent);
  }

  private void setupRecyclerView()
  {
    VariableScrollSpeedLinearLayoutManager layoutManager = new VariableScrollSpeedLinearLayoutManager(this, 4.0f);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    binding.rvTaskGroupActivity.setLayoutManager(layoutManager);

    adapter = new TaskListAdapter(this, taskListList, this, taskGroupModelList);
    binding.rvTaskGroupActivity.setAdapter(adapter);

    binding.rvTaskGroupActivity.setOnDragListener(new TaskListDragListener());
  }

  private void setActivityColorTheme(int colorId)
  {
    ActionBar actionBar = getSupportActionBar();

    switch(colorId)
    {
      case TaskMasterConstants.RED_BACKGROUND:
      {
        if(actionBar != null)
        {
          TaskMasterUtils.setStatusBarColorHelper(getWindow(), getResources().getColor(R.color.task_dark_red));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_red)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_red));
        }
        break;
      }

      case TaskMasterConstants.GREEN_BACKGROUND:
      {
        if(actionBar != null)
        {
          TaskMasterUtils.setStatusBarColorHelper(getWindow(), getResources().getColor(R.color.task_dark_green));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_green)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_green));
        }
        break;
      }

      case TaskMasterConstants.INDIGO_BACKGROUND:
      {
        if(actionBar != null)
        {
          TaskMasterUtils.setStatusBarColorHelper(getWindow(), getResources().getColor(R.color.task_dark_indigo));
          actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.task_indigo)));
          binding.clTaskGroupActivityRoot.setBackgroundColor(getResources().getColor(R.color.task_light_indigo));
        }
        break;
      }
    }
  }

  private void postUiInitialization()
  {
    // set up the toolbar
    setSupportActionBar(binding.tbTaskGroupActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);

      actionBar.setTitle(taskGroupModel.getTitle());
    }

    setActivityColorTheme(taskGroupModel.getColorKey());

    setupRecyclerView();
  }

  private void updateTaskListWithTaskIndex(String taskListId,
                                           int taskIndex,
                                           String taskGroupId,
                                           DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListModelRoot = String.format("/%s/%s/", getString(R.string.db_task_list_object),
            taskListId);

    String taskGroupTaskListRoot = String.format("/%s/%s/", getString(R.string.db_task_group_task_list_key),
            taskGroupId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListModelRoot + getString(R.string.db_task_list_index_key),
            taskIndex);

    childUpdates.put(taskGroupTaskListRoot + taskListId,
            taskIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  private void addTaskListToTaskGroup(String taskListId,
                                      int taskIndex,
                                      String taskGroupId,
                                      DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListId, taskIndex);

    rootDatabaseReference.child(getString(R.string.db_task_group_task_list_key))
            .child(taskGroupId)
            .updateChildren(childUpdates, completionListener);
  }


  private void removeTaskListFromTaskGroup(String taskListId,
                                           String taskGroupId,
                                           DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(getString(R.string.db_task_group_task_list_key))
            .child(taskGroupId)
            .child(taskListId)
            .removeValue(completionListener);
  }

  private void updateTaskListCardWithTaskIndex(String taskListCardId,
                                               int taskIndex,
                                               DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListCardModelRoot = String.format("/%s/%s/", getString(R.string.db_task_list_card_object_key),
            taskListCardId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListCardModelRoot + getString(R.string.db_task_list_card_task_index_key),
            taskIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  private void fetchRemoteData()
  {
    // Grab the task lists from the database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Grab all the task list ids that are associated with the specified task group
    databaseReference.child(getString(R.string.db_task_group_task_list_key)).child(taskGroupModel.getId())
            .orderByValue()
            .addListenerForSingleValueEvent(new ValueEventListener()
            {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot)
              {
                for(DataSnapshot taskGroupTaskListSnapshot : dataSnapshot.getChildren())
                {
                  // the value is the task list index within the task group
                  String taskListId = taskGroupTaskListSnapshot.getKey();

                  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                  // grab the task list associated with the specified task list id
                  databaseReference.child(getString(R.string.db_task_list_object)).child(taskListId)
                          .addListenerForSingleValueEvent(new TaskListValueEventListener(taskListId));
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError)
              {
                Log.i("TaskGroupAct", "failed to get task list id from database");
              }
            });
  }

  class TaskListValueEventListener implements ValueEventListener
  {
    private String taskListId;

    TaskListValueEventListener(String taskListId)
    {
      this.taskListId = taskListId;
    }

    @Override
    public void onDataChange(DataSnapshot taskListSnapshot)
    {
      String taskListTitle = taskListSnapshot.child(getString(R.string.db_task_list_title_key)).getValue(String.class);

      Long taskListIndexLong = taskListSnapshot.child(getString(R.string.db_task_list_index_key)).getValue(Long.class);

      int taskListIndex = taskListIndexLong != null ? taskListIndexLong.intValue() : 0;

      TaskListModel taskListModel = new TaskListModel(taskGroupModel.getId(), taskListId, taskListIndex, taskListTitle);

      TaskListModelContainer container = new TaskListModelContainer(taskListModel, new ArrayList<TaskListCardModel>(), new ArrayList<DueDateModel>());

      taskListList.add(container);

      adapter.notifyDataSetChanged();

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // Grab all the task list card ids that are associated with the specified task list
      databaseReference.child(getString(R.string.db_task_list_task_list_card_key)).child(taskListModel.getTaskListId())
              .orderByValue()
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  for(DataSnapshot taskListTaskListCardSnapshot : dataSnapshot.getChildren())
                  {
                    // the value is the index of the card within the task list
                    String taskListCardId = taskListTaskListCardSnapshot.getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    // grab the task list card associated with the specified task list card id
                    databaseReference.child(getString(R.string.db_task_list_card_object_key)).child(taskListCardId)
                            .addListenerForSingleValueEvent(new TaskListCardValueEventListener(taskListCardId));
                  }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                  Log.i("TaskGroupAct", "failed to get task list card id from database");
                }
              });
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("TaskGroupAct", "failed to get task list from database");
    }
  }

  class TaskListCardValueEventListener implements ValueEventListener
  {
    private String taskListCardId;

    TaskListCardValueEventListener(String taskListCardId)
    {
      this.taskListCardId = taskListCardId;
    }

    @Override
    public void onDataChange(DataSnapshot taskListCardSnapshot)
    {
      String taskListCardTitle = taskListCardSnapshot.child(getString(R.string.db_task_list_card_title_key)).getValue(String.class);
      String taskListCardDetailedDescription = taskListCardSnapshot.child(getString(R.string.db_task_list_card_detailed_description_key)).getValue(String.class);

      Long cardIndexLong = taskListCardSnapshot.child(getString(R.string.db_task_list_card_index_key)).getValue(Long.class);

      final int cardIndex = cardIndexLong != null ? cardIndexLong.intValue() : 0;

      Long taskIndexLong = taskListCardSnapshot.child(getString(R.string.db_task_list_card_task_index_key)).getValue(Long.class);

      final int taskIndex = taskIndexLong != null ? taskIndexLong.intValue() : 0;

      TaskListCardModel taskListCardModel = new TaskListCardModel(taskGroupModel.getId(),
              taskListList.get(taskIndex).getTaskListModel().getTaskListId(),
              taskListCardId,
              taskIndex,
              taskListCardTitle,
              taskListCardDetailedDescription,
              cardIndex);

      taskListList.get(taskIndex).getCardList().add(taskListCardModel);

      // Add a null due date for now and add a valid due date if one exists
      taskListList.get(taskIndex).getDueDateModelList().add(null);

      // TODO: You still need to check for checklists, and attachments and set the appropriate icons

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // Grab the due associated with the specified task list card
      databaseReference.child(getString(R.string.db_due_date_object)).child(taskListCardModel.getCardId())
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  // There is only one due date per task list card
                  if(dataSnapshot.hasChildren())
                  {
                    Boolean completedBoolean = dataSnapshot.child(getString(R.string.db_due_date_completed_key)).getValue(Boolean.class);

                    boolean completed = completedBoolean != null ? completedBoolean : false;

                    // Remember we don't care if the due date has been marked completed
                    if(!completed)
                    {
                      Log.i("TaskGroupAct", "found a due date that is not completed");

                      Long dueDateLong = dataSnapshot.child(getString(R.string.db_due_date_due_date_key)).getValue(Long.class);

                      long dueDate = dueDateLong != null ? dueDateLong : 0;

                      String cardId = taskListList.get(taskIndex).getCardList().get(cardIndex).getCardId();
                      taskListList.get(taskIndex).getDueDateModelList().set(cardIndex, new DueDateModel(cardId, dueDate, false));

                      // Notify adapter
                      TaskListListAdapter taskListListAdapter = taskListList.get(taskIndex).getTaskListListAdapter();
                      if(taskListListAdapter != null)
                      {
                        taskListListAdapter.notifyDataSetChanged();
                      }
                    }
                  }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                  Log.i("TaskGroupAct", "failed to grab the due date for task list card");
                }
              });

      // since a task list creates the adapter for its list of cards via on bind view holder, and
      // a task list itself is also an item in a recycler, we must check for null here because the
      // task list might not have been initialized with a call to on bind view holder
      TaskListListAdapter taskListListAdapter = taskListList.get(taskIndex).getTaskListListAdapter();
      if(taskListListAdapter != null)
      {
        taskListListAdapter.notifyDataSetChanged();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("TaskGroupAct", "failed to get task list card from database");
    }
  }

  // https://stackoverflow.com/questions/32241948/how-can-i-control-the-scrolling-speed-of-recyclerview-smoothscrolltopositionpos
  public class VariableScrollSpeedLinearLayoutManager extends LinearLayoutManager
  {
    private final float factor;

    VariableScrollSpeedLinearLayoutManager(Context context, float factor)
    {
      super(context);
      this.factor = factor;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
    {
      final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext())
      {
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition)
        {
          return VariableScrollSpeedLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
        {
          return super.calculateSpeedPerPixel(displayMetrics) * factor;
        }
      };

      linearSmoothScroller.setTargetPosition(position);
      startSmoothScroll(linearSmoothScroller);
    }
  }

  /**
   * Allows the TaskGroupActivity to scroll horizontally while a card is being dragged.
   */
  class TaskListDragListener implements View.OnDragListener
  {
    /**
     * The number of points within reaching either the +/- horizontal edge of the screen before a
     * scroll is considered.
     */
    private static final int HORIZONTAL_SCROLL_THRESHOLD = 10;

    @Override
    public boolean onDrag(View v, DragEvent event)
    {
      switch(event.getAction())
      {
        case DragEvent.ACTION_DRAG_STARTED:
        {
          // we need to accept the drag so we can begin scrolling, so return true
          return true;
        }

        case DragEvent.ACTION_DRAG_LOCATION:
        {
          // Find the view at the coordinate and scroll to it if you are not already there
          View view = binding.rvTaskGroupActivity.findChildViewUnder(event.getX(), 0);

          // using width here although measured width seems to return the same value
          int width = binding.rvTaskGroupActivity.getWidth();

          if(view != null)
          {
            Rect viewVisibleRect = new Rect();
            view.getGlobalVisibleRect(viewVisibleRect);

            int pos = binding.rvTaskGroupActivity.getChildAdapterPosition(view);

            if((event.getX() + TaskListDragListener.HORIZONTAL_SCROLL_THRESHOLD) > width)
            {
              if(viewVisibleRect.width() < view.getWidth())
              {
                // the current view is not fully visible, scroll to it
                binding.rvTaskGroupActivity.smoothScrollToPosition(pos);
              }
              else
              {
                // move to the next child in the adapter if and only if there are any more.
                int nextChildPos = pos + 1;

                int lastChildPos = binding.rvTaskGroupActivity.getAdapter().getItemCount() - 1;

                if(nextChildPos <= lastChildPos)
                {
                  binding.rvTaskGroupActivity.smoothScrollToPosition(nextChildPos);
                }
              }
            }
            else if((event.getX() - TaskListDragListener.HORIZONTAL_SCROLL_THRESHOLD) < 0)
            {
              if(viewVisibleRect.width() < view.getWidth())
              {
                // the current view is not fully visible, scroll to it
                binding.rvTaskGroupActivity.smoothScrollToPosition(pos);
              }
              else
              {
                // move to the previous child in the adapter if and only if there are any more.
                int previousChildPos = pos - 1;

                if(previousChildPos >= 0)
                {
                  binding.rvTaskGroupActivity.smoothScrollToPosition(previousChildPos);
                }
              }
            }
          }
        }
        break;
      }

      return false;
    }
  }
}
