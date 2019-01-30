package com.example.android.taskmaster.view.widget;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DueDateFetchIntentService extends IntentService
{
  public static final String ACTION_DUE_DATE_RESP_NO_DATA = "com.example.android.taskmaster.action.DUE_DATE_FETCHED_NO_DATA";
  public static final String ACTION_DUE_DATE_RESP_WITH_DATA = "com.example.android.taskmaster.action.DUE_DATE_FETCHED_WITH_DATA";

  public static Intent getStartIntent(Context context)
  {
    return new Intent(context, DueDateFetchIntentService.class);
  }

  public DueDateFetchIntentService()
  {
    super("DueDateFetchIntentService");
  }

  private void startDueDateFetch()
  {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    if(firebaseAuth.getCurrentUser() != null)
    {
      String currentUserEmailAddress = firebaseAuth.getCurrentUser().getEmail();

      // Handle null which means no email associated with the user.
      // This should never happen since if we are logged in, we have an email address.
      if(currentUserEmailAddress != null)
      {
        // Firebase does not allow a key to contain a period "."
        String firebaseSafeEmailKey = currentUserEmailAddress.replace(".", ",");

        // Grab all the task group ids where the current user is a member
        databaseReference.child(getString(R.string.db_task_group_user)).child(firebaseSafeEmailKey)
                .addListenerForSingleValueEvent(new TaskGroupUserHandler());
      }
    }
  }

  class TaskGroupUserHandler implements ValueEventListener
  {
    // This is need for adding to the back stack
    private List<TaskGroupModel> taskGroupModelList;

    TaskGroupUserHandler()
    {
      taskGroupModelList = new ArrayList<>();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      if(dataSnapshot.hasChildren())
      {
        for(DataSnapshot taskGroupUserSnapshot : dataSnapshot.getChildren())
        {
          // the value is a boolean which we do not care about
          String taskGroupId = taskGroupUserSnapshot.getKey();

          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

          // grab the task group associated with the specified task group id
          databaseReference.child(getString(R.string.db_task_group_object)).child(taskGroupId)
                  .addListenerForSingleValueEvent(new TaskGroupHandler(taskGroupId, taskGroupModelList));
        }
      }
      else
      {
        // the user doesn't have any task groups yet, we are done
        DueDateFetchIntentService.this.notifyWidget();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskGroupUserHandler: failed to get task group id");
    }
  }

  class TaskGroupHandler implements ValueEventListener
  {
    private String taskGroupId;
    private List<TaskGroupModel> taskGroupModelList;

    TaskGroupHandler(String taskGroupId, List<TaskGroupModel> taskGroupModelList)
    {
      this.taskGroupId = taskGroupId;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      String taskGroupTitle = dataSnapshot.child(getString(R.string.db_task_group_object_title))
              .getValue(String.class);

      Long taskGroupColorKeyLong = dataSnapshot.child(getString(R.string.db_task_group_object_color_key))
                .getValue(Long.class);

      int taskGroupColorKey = taskGroupColorKeyLong != null ? taskGroupColorKeyLong.intValue() : 0;

      TaskGroupModel taskGroupModel = new TaskGroupModel(taskGroupId, taskGroupTitle, taskGroupColorKey);

      taskGroupModelList.add(taskGroupModel);

      ContentValues contentValues = new ContentValues();
      contentValues.put(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_ID, taskGroupId);
      contentValues.put(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_TITLE, taskGroupTitle);
      contentValues.put(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_COLOR_KEY, taskGroupColorKey);

      // I don't need the content provider for task groups, the list would suffice. However, I'm
      // leaving it in for now.
      getApplicationContext().getContentResolver().insert(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.CONTENT_URI, contentValues);

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // grab all the task list ids associated with this task group
      databaseReference.child(getString(R.string.db_task_group_task_list_key)).child(taskGroupId)
                .addListenerForSingleValueEvent(new TaskGroupTaskListHandler(taskGroupId,
                        taskGroupTitle,
                        taskGroupColorKey,
                        taskGroupModelList));
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskGroupHandler: failed to get task group");
    }
  }

  class TaskGroupTaskListHandler implements ValueEventListener
  {
    private String taskGroupId;
    private String taskGroupTitle;
    private int taskGroupColorKey;
    private List<TaskGroupModel> taskGroupModelList;

    TaskGroupTaskListHandler(String taskGroupId,
                             String taskGroupTitle,
                             int taskGroupColorKey,
                             List<TaskGroupModel> taskGroupModelList)
    {

      this.taskGroupId = taskGroupId;
      this.taskGroupTitle = taskGroupTitle;
      this.taskGroupColorKey = taskGroupColorKey;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      if(dataSnapshot.hasChildren())
      {
        for(DataSnapshot taskListIdSnapshot : dataSnapshot.getChildren())
        {
          String taskListId = taskListIdSnapshot.getKey();

          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

          // grab the task list associated with this task list id
          databaseReference.child(getString(R.string.db_task_list_object))
                  .child(taskListId)
                  .addListenerForSingleValueEvent(new TaskListHandler(taskGroupId,
                          taskGroupTitle,
                          taskGroupColorKey,
                          taskListId,
                          taskGroupModelList));
        }
      }
      else
      {
        // there are no task lists associated with this task group, we are done
        DueDateFetchIntentService.this.notifyWidget();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskGroupTaskListHandler: failed to get task list id");
    }
  }

  class TaskListHandler implements ValueEventListener
  {
    private String taskGroupId;
    private String taskGroupTitle;
    private int taskGroupColorKey;
    private String taskListId;
    private List<TaskGroupModel> taskGroupModelList;

    TaskListHandler(String taskGroupId,
                    String taskGroupTitle,
                    int taskGroupColorKey,
                    String taskListId,
                    List<TaskGroupModel> taskGroupModelList)
    {

      this.taskGroupId = taskGroupId;
      this.taskGroupTitle = taskGroupTitle;
      this.taskGroupColorKey = taskGroupColorKey;
      this.taskListId = taskListId;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      String taskListTitle = dataSnapshot.child(getString(R.string.db_task_list_title_key))
              .getValue(String.class);

      Long taskListIndexLong = dataSnapshot.child(getString(R.string.db_task_list_index_key))
                .getValue(Long.class);

      int taskListIndex = taskListIndexLong != null ? taskListIndexLong.intValue() : 0;

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // grab all of the task list card ids associated with this task list
      databaseReference.child(getString(R.string.db_task_list_task_list_card_key))
                .child(taskListId)
                .addListenerForSingleValueEvent(new TaskListTaskListCardHandler(taskGroupId,
                        taskGroupTitle,
                        taskGroupColorKey,
                        taskListId,
                        taskListTitle,
                        taskListIndex,
                        taskGroupModelList));
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskListHandler: failed to get task list");
    }
  }

  class TaskListTaskListCardHandler implements ValueEventListener
  {
    private String taskGroupId;
    private String taskGroupTitle;
    private int taskGroupColorKey;
    private String taskListId;
    private String taskListTitle;
    private int taskListIndex;
    private List<TaskGroupModel> taskGroupModelList;

    TaskListTaskListCardHandler(String taskGroupId,
                                String taskGroupTitle,
                                int taskGroupColorKey,
                                String taskListId,
                                String taskListTitle,
                                int taskListIndex,
                                List<TaskGroupModel> taskGroupModelList)
    {

      this.taskGroupId = taskGroupId;
      this.taskGroupTitle = taskGroupTitle;
      this.taskGroupColorKey = taskGroupColorKey;
      this.taskListId = taskListId;
      this.taskListTitle = taskListTitle;
      this.taskListIndex = taskListIndex;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      if(dataSnapshot.hasChildren())
      {
        for(DataSnapshot taskListCardIdSnapshot : dataSnapshot.getChildren())
        {
          String taskListCardId = taskListCardIdSnapshot.getKey();

          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

          // grab the task list card associated with this task list card id
          databaseReference.child(getString(R.string.db_task_list_card_object_key))
                  .child(taskListCardId)
                  .addListenerForSingleValueEvent(new TaskListCardHandler(taskGroupId,
                          taskGroupTitle,
                          taskGroupColorKey,
                          taskListId,
                          taskListTitle,
                          taskListIndex,
                          taskListCardId,
                          taskGroupModelList));
        }
      }
      else
      {
        // this task list has no cards, we are done
        DueDateFetchIntentService.this.notifyWidget();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskListTaskListCardHandler: failed to get task list card id");
    }
  }

  class TaskListCardHandler implements ValueEventListener
  {
    private String taskGroupId;
    private String taskGroupTitle;
    private int taskGroupColorKey;
    private String taskListId;
    private String taskListTitle;
    private int taskListIndex;
    private String taskListCardId;
    private List<TaskGroupModel> taskGroupModelList;

    TaskListCardHandler(String taskGroupId,
                        String taskGroupTitle,
                        int taskGroupColorKey,
                        String taskListId,
                        String taskListTitle,
                        int taskListIndex,
                        String taskListCardId,
                        List<TaskGroupModel> taskGroupModelList)
    {

      this.taskGroupId = taskGroupId;
      this.taskGroupTitle = taskGroupTitle;
      this.taskGroupColorKey = taskGroupColorKey;
      this.taskListId = taskListId;
      this.taskListTitle = taskListTitle;
      this.taskListIndex = taskListIndex;
      this.taskListCardId = taskListCardId;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      String taskListCardTitle = dataSnapshot.child(getString(R.string.db_task_list_card_title_key))
              .getValue(String.class);

      String taskListCardDetailed = dataSnapshot.child(getString(R.string.db_task_list_card_detailed_description_key))
              .getValue(String.class);

      Long taskListCardIndexLong = dataSnapshot.child(getString(R.string.db_task_list_card_index_key))
              .getValue(Long.class);

      int taskListCardIndex = taskListCardIndexLong != null ? taskListCardIndexLong.intValue() : 0;

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // grab the due date associated with this task list card if any
      databaseReference.child(getString(R.string.db_due_date_object))
              .child(taskListCardId)
              .addListenerForSingleValueEvent(new DueDateNotCompletedHandler(taskGroupId,
                      taskGroupTitle,
                      taskGroupColorKey,
                      taskListId,
                      taskListTitle,
                      taskListIndex,
                      taskListCardId,
                      taskListCardTitle,
                      taskListCardDetailed,
                      taskListCardIndex,
                      taskGroupModelList));
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "TaskListCardHandler: failed to get task list card");
    }
  }

  class DueDateNotCompletedHandler implements ValueEventListener
  {
    private String taskGroupId;
    private String taskGroupTitle;
    private int taskGroupColorKey;
    private String taskListId;
    private String taskListTitle;
    private int taskListIndex;
    private String taskListCardId;
    private String taskListCardTitle;
    private String taskListCardDetailed;
    private int taskListCardIndex;
    private List<TaskGroupModel> taskGroupModelList;

    DueDateNotCompletedHandler(String taskGroupId,
                               String taskGroupTitle,
                               int taskGroupColorKey,
                               String taskListId,
                               String taskListTitle,
                               int taskListIndex,
                               String taskListCardId,
                               String taskListCardTitle,
                               String taskListCardDetailed,
                               int taskListCardIndex,
                               List<TaskGroupModel> taskGroupModelList)
    {

      this.taskGroupId = taskGroupId;
      this.taskGroupTitle = taskGroupTitle;
      this.taskGroupColorKey = taskGroupColorKey;
      this.taskListId = taskListId;
      this.taskListTitle = taskListTitle;
      this.taskListIndex = taskListIndex;
      this.taskListCardId = taskListCardId;
      this.taskListCardTitle = taskListCardTitle;
      this.taskListCardDetailed = taskListCardDetailed;
      this.taskListCardIndex = taskListCardIndex;
      this.taskGroupModelList = taskGroupModelList;
    }

    @Override
    public void onDataChange(DataSnapshot dueDateSnapshot)
    {
      if(dueDateSnapshot.hasChildren())
      {
        Long dueDateLong = dueDateSnapshot.child(getString(R.string.db_due_date_due_date_key)).getValue(Long.class);

        long dueDate = dueDateLong != null ? dueDateLong : 0;

        Boolean completedBoolean = dueDateSnapshot.child(getString(R.string.db_due_date_completed_key)).getValue(Boolean.class);

        boolean completed = completedBoolean != null ? completedBoolean : false;

        if(!completed)
        {
          ContentValues contentValues = new ContentValues();
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_ID, taskListCardId);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_TITLE, taskListCardTitle);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_DETAILED, taskListCardDetailed);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_INDEX, taskListCardIndex);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_INDEX, taskListIndex);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_ID, taskGroupId);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_TITLE, taskGroupTitle);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_COLOR_KEY, taskGroupColorKey);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_ID, taskListId);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_TITLE, taskListTitle);
          contentValues.put(DueDateWidgetContract.DueDateWidgetColumns.DUE_DATE, dueDate);

          getApplicationContext().getContentResolver().insert(DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI, contentValues);

          TaskMasterUtils.notifyWidget(DueDateFetchIntentService.this,
                  taskGroupModelList,
                  DueDateFetchIntentService.ACTION_DUE_DATE_RESP_WITH_DATA);
        }
        else
        {
          // due date completed already
          DueDateFetchIntentService.this.notifyWidget();
        }
      }
      else
      {
        // no due dates
        DueDateFetchIntentService.this.notifyWidget();
      }

    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DueDateFetchService", "DueDateNotCompletedHandler: failed to get due date");
    }
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent)
  {
    startDueDateFetch();
  }

  private void notifyWidget()
  {
    Intent broadcastIntent = new Intent(getApplicationContext(), TaskMasterWidgetProvider.class);
    broadcastIntent.setAction(DueDateFetchIntentService.ACTION_DUE_DATE_RESP_NO_DATA);
    sendBroadcast(broadcastIntent);
  }
}
