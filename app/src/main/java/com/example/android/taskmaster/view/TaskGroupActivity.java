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
            new ArrayList<TaskListCardModel>());

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
            true);

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
      // TODO: Actually perform the task list move within the database, change indexes too

      // remove the task list from the list, change indexes too
      taskListList.remove(taskListIndex);

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

  private void fetchRemoteData()
  {
    // Grab the task lists from the database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Grab all the task list ids that are associated with the specified task group
    databaseReference.child(getString(R.string.db_task_group_task_list_key)).child(taskGroupModel.getId())
            .addListenerForSingleValueEvent(new ValueEventListener()
            {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot)
              {
                for(DataSnapshot taskGroupTaskListSnapshot : dataSnapshot.getChildren())
                {
                  // the value is a boolean which we do not care about
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

      TaskListModelContainer container = new TaskListModelContainer(taskListModel, new ArrayList<TaskListCardModel>());

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // Grab all the task list card ids that are associated with the specified task list
      databaseReference.child(getString(R.string.db_task_list_task_list_card_key)).child(taskListModel.getTaskListId())
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  for(DataSnapshot taskListTaskListCardSnapshot : dataSnapshot.getChildren())
                  {
                    // the value is a boolean which we do not care about
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

      taskListList.add(container);

      adapter.notifyDataSetChanged();
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

      int cardIndex = cardIndexLong != null ? cardIndexLong.intValue() : 0;

      Long taskIndexLong = taskListCardSnapshot.child(getString(R.string.db_task_list_card_task_index_key)).getValue(Long.class);

      int taskIndex = taskIndexLong != null ? taskIndexLong.intValue() : 0;

      TaskListCardModel taskListCardModel = new TaskListCardModel(taskGroupModel.getId(),
              taskListList.get(taskIndex).getTaskListModel().getTaskListId(),
              taskListCardId,
              taskIndex,
              taskListCardTitle,
              taskListCardDetailedDescription,
              cardIndex);

      taskListList.get(taskIndex).getCardList().add(taskListCardModel);

      // TODO: You still need to check for due dates, checklists, and attachments and set the appropriate icons

      taskListList.get(taskIndex).getTaskListListAdapter().notifyDataSetChanged();
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
