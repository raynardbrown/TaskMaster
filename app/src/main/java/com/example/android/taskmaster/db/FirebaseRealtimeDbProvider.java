package com.example.android.taskmaster.db;

import android.content.Context;
import android.util.Log;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.AttachmentCreationInfo;
import com.example.android.taskmaster.model.AttachmentExtraDataModel;
import com.example.android.taskmaster.model.AttachmentInfo;
import com.example.android.taskmaster.model.AttachmentModel;
import com.example.android.taskmaster.model.ChecklistCompletionCounter;
import com.example.android.taskmaster.model.TaskListModelContainer;
import com.example.android.taskmaster.view.CardDetailAttachmentAdapter;
import com.example.android.taskmaster.view.IAttachmentFetchListener;
import com.example.android.taskmaster.view.TaskListListAdapter;
import com.example.android.taskmaster.view.dialog.IChooseAttachmentDialogListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRealtimeDbProvider
{
  private FirebaseRealtimeDbProvider()
  {

  }

  public static void addTaskListToTaskGroup(Context context,
                                            String taskListId,
                                            int taskIndex,
                                            String taskGroupId,
                                            DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListId, taskIndex);

    rootDatabaseReference.child(context.getString(R.string.db_task_group_task_list_key))
            .child(taskGroupId)
            .updateChildren(childUpdates, completionListener);
  }

  public static void updateTaskListWithTaskIndex(Context context,
                                                 String taskListId,
                                                 int taskIndex,
                                                 String taskGroupId,
                                                 DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListModelRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_object),
            taskListId);

    String taskGroupTaskListRoot = String.format("/%s/%s/", context.getString(R.string.db_task_group_task_list_key),
            taskGroupId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListModelRoot + context.getString(R.string.db_task_list_index_key),
            taskIndex);

    childUpdates.put(taskGroupTaskListRoot + taskListId,
            taskIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void removeTaskListFromTaskGroup(Context context,
                                                 String taskListId,
                                                 String taskGroupId,
                                                 DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_task_group_task_list_key))
            .child(taskGroupId)
            .child(taskListId)
            .removeValue(completionListener);
  }

  public static void addTaskListCard(Context context,
                                     String taskListCardId,
                                     int taskListCardIndex,
                                     String taskListCardTitle,
                                     String taskListCardDetailed,
                                     int taskListIndex,
                                     DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(context.getString(R.string.db_task_list_card_index_key), taskListCardIndex);
    childUpdates.put(context.getString(R.string.db_task_list_card_title_key), taskListCardTitle);
    childUpdates.put(context.getString(R.string.db_task_list_card_detailed_description_key), taskListCardDetailed);
    childUpdates.put(context.getString(R.string.db_task_list_card_task_index_key), taskListIndex);

    rootDatabaseReference.child(context.getString(R.string.db_task_list_card_object_key))
            .child(taskListCardId)
            .updateChildren(childUpdates, completionListener);
  }

  public static void addTaskListCardToTaskList(Context context,
                                               String taskListCardId,
                                               int taskListCardIndex,
                                               String taskListId,
                                               DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListCardId, taskListCardIndex);

    rootDatabaseReference.child(context.getString(R.string.db_task_list_task_list_card_key))
            .child(taskListId)
            .updateChildren(childUpdates, completionListener);
  }

  public static void updateTaskListCardWithTaskIndex(Context context,
                                                     String taskListCardId,
                                                     int taskIndex,
                                                     DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListCardModelRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_card_object_key),
            taskListCardId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListCardModelRoot + context.getString(R.string.db_task_list_card_task_index_key),
            taskIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void updateTaskListCardWithIndex(Context context,
                                                 String taskListCardId,
                                                 int taskListCardIndex,
                                                 String taskListId,
                                                 DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String taskListCardModelRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_card_object_key),
            taskListCardId);

    String taskListTaskListCardRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_task_list_card_key),
            taskListId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(taskListCardModelRoot + context.getString(R.string.db_task_list_card_index_key),
            taskListCardIndex);

    childUpdates.put(taskListTaskListCardRoot + taskListCardId,
            taskListCardIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void removeTaskListCardFromTaskList(Context context,
                                                    String taskListCardId,
                                                    String taskListId,
                                                    DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_task_list_task_list_card_key))
            .child(taskListId)
            .child(taskListCardId)
            .removeValue(completionListener);
  }

  public static void removeTaskListCard(Context context,
                                        String taskListCardId,
                                        DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_task_list_card_object_key))
            .child(taskListCardId)
            .removeValue(completionListener);
  }

  public static void addAttachment(Context context,
                                   AttachmentModel attachmentModel,
                                   DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String attachmentModelRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_object),
            attachmentModel.getAttachmentId());

    String taskListCardAttachmentRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_card_attachment_key),
            attachmentModel.getCardId());

    String attachmentBoundRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_attachment_bound_key),
            attachmentModel.getAttachmentId());

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_data_key),
            attachmentModel.getAttachmentData());

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_index_key),
            attachmentModel.getAttachmentIndex());

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_time_key),
            attachmentModel.getAttachmentTime());

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_type_key),
            attachmentModel.getAttachmentType());

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_bound_key),
            attachmentModel.isBound());

    // link the attachment to the task list card
    childUpdates.put(taskListCardAttachmentRoot + attachmentModel.getAttachmentId(),
            attachmentModel.getAttachmentIndex());

    // add the attachment to the attachment bound node
    childUpdates.put(attachmentBoundRoot, attachmentModel.isBound());

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void addAttachmentExtraData(Context context,
                                            String attachmentId,
                                            AttachmentExtraDataModel attachmentExtraDataModel,
                                            DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String attachmentExtraDataModelRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_extra_data_object),
            attachmentId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(attachmentExtraDataModelRoot + context.getString(R.string.db_attachment_extra_data_data_key),
            attachmentExtraDataModel.getAttachmentExtraData());

    childUpdates.put(attachmentExtraDataModelRoot + context.getString(R.string.db_attachment_extra_data_type_key),
            attachmentExtraDataModel.getAttachmentExtraType());

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void updateChecklistWithIndex(Context context,
                                              String checklistId,
                                              int newIndex,
                                              String taskListCardId,
                                              DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String checklistModelRoot = String.format("/%s/%s/", context.getString(R.string.db_checklist_object),
            checklistId);

    String taskListCardChecklistRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_card_checklist_key),
            taskListCardId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(checklistModelRoot + context.getString(R.string.db_checklist_index_key),
            newIndex);

    childUpdates.put(taskListCardChecklistRoot + checklistId,
            newIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void updateChecklistItemWithIndex(Context context,
                                                  String checklistItemId,
                                                  int newIndex,
                                                  String checklistId,
                                                  DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String checklistItemModelRoot = String.format("/%s/%s/", context.getString(R.string.db_checklist_item_object),
            checklistItemId);

    String checklistChecklistItemRoot = String.format("/%s/%s/", context.getString(R.string.db_checklist_checklist_item_key),
            checklistId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(checklistItemModelRoot + context.getString(R.string.db_checklist_item_index_key),
            newIndex);

    childUpdates.put(checklistChecklistItemRoot + checklistItemId,
            newIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void removeChecklistItemFromChecklist(Context context,
                                                      String checklistItemId,
                                                      String checklistId,
                                                      DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_checklist_checklist_item_key))
            .child(checklistId)
            .child(checklistItemId)
            .removeValue(completionListener);
  }

  public static void removeChecklistItem(Context context,
                                         String checklistItemId,
                                         DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_checklist_item_object))
            .child(checklistItemId)
            .removeValue(completionListener);
  }

  public static void removeChecklistFromTaskListCard(Context context,
                                                     String checklistId,
                                                     String taskListCardId,
                                                     DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_task_list_card_checklist_key))
            .child(taskListCardId)
            .child(checklistId)
            .removeValue(completionListener);
  }

  public static void removeChecklist(Context context,
                                     String checklistId,
                                     DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_checklist_object))
            .child(checklistId)
            .removeValue(completionListener);
  }

  public static void removeAttachmentExtraData(Context context,
                                               String attachmentId,
                                               DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_attachment_extra_data_object))
            .child(attachmentId)
            .removeValue(completionListener);
  }

  public static void removeAttachmentFromTaskListCard(Context context,
                                                      String attachmentId,
                                                      String taskListCardId,
                                                      DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_task_list_card_attachment_key))
            .child(taskListCardId)
            .child(attachmentId)
            .removeValue(completionListener);
  }

  public static void removeAttachmentFromAttachmentBound(Context context,
                                                         String attachmentId,
                                                         DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_attachment_attachment_bound_key))
            .child(attachmentId)
            .removeValue(completionListener);
  }

  public static void removeAttachment(Context context,
                                      String attachmentId,
                                      DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    rootDatabaseReference.child(context.getString(R.string.db_attachment_object))
            .child(attachmentId)
            .removeValue(completionListener);
  }

  public static void updateAttachmentWithIndex(Context context,
                                               String attachmentId,
                                               int attachmentIndex,
                                               String taskListCardId,
                                               DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String attachmentModelRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_object),
            attachmentId);

    String taskListCardAttachmentRoot = String.format("/%s/%s/", context.getString(R.string.db_task_list_card_attachment_key),
            taskListCardId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_index_key),
            attachmentIndex);

    childUpdates.put(taskListCardAttachmentRoot + attachmentId,
            attachmentIndex);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  public static void setAttachmentBound(Context context,
                                        String attachmentId,
                                        boolean bound,
                                        DatabaseReference.CompletionListener completionListener)
  {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootDatabaseReference = database.getReference();

    String attachmentModelRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_object),
            attachmentId);

    String attachmentBoundRoot = String.format("/%s/%s/", context.getString(R.string.db_attachment_attachment_bound_key),
            attachmentId);

    Map<String, Object> childUpdates = new HashMap<>();

    childUpdates.put(attachmentModelRoot + context.getString(R.string.db_attachment_bound_key),
            bound);

    childUpdates.put(attachmentBoundRoot, bound);

    rootDatabaseReference.updateChildren(childUpdates, completionListener);
  }

  // Fetch handlers

  static class TaskListCardAttachmentIdValueHandler implements ValueEventListener
  {
    private Context context;
    private String taskListCardId;
    private int taskIndex;
    private int cardIndex;
    private List<TaskListModelContainer> taskListModelContainerList;
    long numberOfAttachments;
    long currentAttachment;
    AttachmentInfo attachmentInfo;

    TaskListCardAttachmentIdValueHandler(Context context,
                                         String taskListCardId,
                                         int taskIndex,
                                         int cardIndex,
                                         List<TaskListModelContainer> taskListModelContainerList)
    {
      this.context = context;
      this.taskListCardId = taskListCardId;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.taskListModelContainerList = taskListModelContainerList;
      numberOfAttachments = 0;
      currentAttachment = 0;
      attachmentInfo = new AttachmentInfo();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      numberOfAttachments = dataSnapshot.getChildrenCount();

      for(DataSnapshot taskListCardAttachmentSnapshot : dataSnapshot.getChildren())
      {
        ++currentAttachment;
        ++(attachmentInfo.attachmentCount);

        String attachmentId = taskListCardAttachmentSnapshot.getKey();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // get the bound attachment property value associated with this attachment id
        databaseReference.child(context.getString(R.string.db_attachment_attachment_bound_key))
                .child(attachmentId)
                .addListenerForSingleValueEvent(new AttachmentBoundValueHandler(context,
                        taskListCardId,
                        attachmentId,
                        taskIndex,
                        cardIndex,
                        attachmentInfo,
                        numberOfAttachments,
                        currentAttachment,
                        taskListModelContainerList));
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to get attachment id from database");
    }
  }

  static class AttachmentBoundValueHandler implements ValueEventListener
  {
    private Context context;
    private String taskListCardId;
    private String attachmentId;
    private int taskIndex;
    private int cardIndex;
    private AttachmentInfo attachmentInfo;
    private long numberOfAttachments;
    private long currentAttachment;
    private List<TaskListModelContainer> taskListModelContainerList;

    AttachmentBoundValueHandler(Context context,
                                String taskListCardId,
                                String attachmentId,
                                int taskIndex,
                                int cardIndex,
                                AttachmentInfo attachmentInfo,
                                long numberOfAttachments,
                                long currentAttachment,
                                List<TaskListModelContainer> taskListModelContainerList)
    {
      this.context = context;
      this.taskListCardId = taskListCardId;
      this.attachmentId = attachmentId;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.attachmentInfo = attachmentInfo;
      this.numberOfAttachments = numberOfAttachments;
      this.currentAttachment = currentAttachment;
      this.taskListModelContainerList = taskListModelContainerList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      Boolean boundBoolean = dataSnapshot.getValue(Boolean.class);
      boolean bound = boundBoolean != null ? boundBoolean : false;

      if(bound)
      {
        // we found the attachment with the bound property set true

        // get the bound attachment and set it to the attachment info
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(context.getString(R.string.db_attachment_object))
                .child(attachmentId)
                .addListenerForSingleValueEvent(new BoundAttachmentHandler(context,
                        attachmentId,
                        taskListCardId,
                        taskIndex,
                        cardIndex,
                        attachmentInfo,
                        taskListModelContainerList));
      }
      else
      {
        if(numberOfAttachments == currentAttachment)
        {
          // we are done and we did not find a bound attachment

          // just create an attachment info without a bound attachment
          taskListModelContainerList.get(taskIndex).getAttachmentInfoList().set(cardIndex, attachmentInfo);

          // Notify the adapter
          TaskListListAdapter taskListListAdapter = taskListModelContainerList.get(taskIndex).getTaskListListAdapter();
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
      Log.i("DbProvider", "failed to get attachment bound value from database");
    }
  }

  static class BoundAttachmentHandler implements ValueEventListener
  {
    private Context context;
    private String attachmentId;
    private String taskListCardId;
    private int taskIndex;
    private int cardIndex;
    private AttachmentInfo attachmentInfo;
    private List<TaskListModelContainer> taskListModelContainerList;

    BoundAttachmentHandler(Context context,
                           String attachmentId,
                           String taskListCardId,
                           int taskIndex,
                           int cardIndex,
                           AttachmentInfo attachmentInfo,
                           List<TaskListModelContainer> taskListModelContainerList)
    {
      this.context = context;
      this.attachmentId = attachmentId;
      this.taskListCardId = taskListCardId;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.attachmentInfo = attachmentInfo;
      this.taskListModelContainerList = taskListModelContainerList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      String attachmentData = dataSnapshot.child(context.getString(R.string.db_attachment_data_key)).getValue(String.class);

      Long attachmentIndexLong = dataSnapshot.child(context.getString(R.string.db_attachment_index_key)).getValue(Long.class);

      int attachmentIndex = attachmentIndexLong != null ? attachmentIndexLong.intValue() : 0;

      Long attachmentTimeLong = dataSnapshot.child(context.getString(R.string.db_attachment_time_key)).getValue(Long.class);

      long attachmentTime = attachmentTimeLong != null ? attachmentTimeLong : 0;

      Long attachmentTypeLong = dataSnapshot.child(context.getString(R.string.db_attachment_type_key)).getValue(Long.class);

      int attachmentType = attachmentTypeLong != null ? attachmentTypeLong.intValue() : 0;

      Boolean attachmentBoundBoolean = dataSnapshot.child(context.getString(R.string.db_attachment_bound_key)).getValue(Boolean.class);

      Boolean attachmentBound = attachmentBoundBoolean != null ? attachmentBoundBoolean : false;

      attachmentInfo.boundAttachmentModel = new AttachmentModel(taskListCardId,
              attachmentId,
              attachmentData,
              attachmentIndex,
              attachmentTime,
              attachmentType,
              attachmentBound);

      taskListModelContainerList.get(taskIndex).getAttachmentInfoList().set(cardIndex, attachmentInfo);

      // Notify the adapter
      TaskListListAdapter taskListListAdapter = taskListModelContainerList.get(taskIndex).getTaskListListAdapter();
      if(taskListListAdapter != null)
      {
        taskListListAdapter.notifyDataSetChanged();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to get the bound attachment from database");
    }
  }

  static class ChecklistIdValueHandler implements ValueEventListener
  {
    private Context context;
    private int taskIndex;
    private int cardIndex;
    private List<TaskListModelContainer> taskListModelContainerList;
    private long numberOfChecklists;
    private long currentChecklist;
    private long numberOfChecklistItems;
    private long currentChecklistItem;
    private ChecklistCompletionCounter checklistCompletionCounter;

    ChecklistIdValueHandler(Context context,
                            int taskIndex,
                            int cardIndex,
                            List<TaskListModelContainer> taskListModelContainerList)
    {
      this.context = context;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.taskListModelContainerList = taskListModelContainerList;
      numberOfChecklists = 0;
      currentChecklist = 0;
      numberOfChecklistItems = 0;
      currentChecklistItem = 0;

      checklistCompletionCounter = new ChecklistCompletionCounter();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      numberOfChecklists = dataSnapshot.getChildrenCount();

      // grab all the checklists associated with this task list card
      for(DataSnapshot taskListCardChecklistSnapshot : dataSnapshot.getChildren())
      {
        ++currentChecklist;

        String checklistId = taskListCardChecklistSnapshot.getKey();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(context.getString(R.string.db_checklist_checklist_item_key)).child(checklistId)
                .orderByValue()
                .addListenerForSingleValueEvent(new ChecklistItemIdValueHandler(context,
                        taskIndex,
                        cardIndex,
                        numberOfChecklists,
                        currentChecklist,
                        numberOfChecklistItems,
                        currentChecklistItem,
                        checklistCompletionCounter,
                        taskListModelContainerList));
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab the checklist for task list card");
    }
  }

  static class ChecklistItemIdValueHandler implements ValueEventListener
  {
    private Context context;
    private int taskIndex;
    private int cardIndex;
    private long numberOfChecklists;
    private long currentChecklist;
    private long numberOfChecklistItems;
    private long currentChecklistItem;
    private ChecklistCompletionCounter checklistCompletionCounter;
    private List<TaskListModelContainer> taskListModelContainerList;

    ChecklistItemIdValueHandler(Context context,
                                int taskIndex,
                                int cardIndex,
                                long numberOfChecklists,
                                long currentChecklist,
                                long numberOfChecklistItems,
                                long currentChecklistItem,
                                ChecklistCompletionCounter checklistCompletionCounter,
                                List<TaskListModelContainer> taskListModelContainerList)
    {

      this.context = context;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.numberOfChecklists = numberOfChecklists;
      this.currentChecklist = currentChecklist;
      this.numberOfChecklistItems = numberOfChecklistItems;
      this.currentChecklistItem = currentChecklistItem;
      this.checklistCompletionCounter = checklistCompletionCounter;
      this.taskListModelContainerList = taskListModelContainerList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      numberOfChecklistItems = dataSnapshot.getChildrenCount();

      // reinitialize the currentChecklistItem because this is a new checklist
      currentChecklistItem = 0;

      for(DataSnapshot checklistChecklistItemSnapshot : dataSnapshot.getChildren())
      {
        ++currentChecklistItem;

        String checklistItemId = checklistChecklistItemSnapshot.getKey();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(context.getString(R.string.db_checklist_item_object)).child(checklistItemId)
                .addListenerForSingleValueEvent(new ChecklistCompletedValueHandler(context,
                        taskIndex,
                        cardIndex,
                        numberOfChecklists,
                        currentChecklist,
                        numberOfChecklistItems,
                        currentChecklistItem,
                        checklistCompletionCounter,
                        taskListModelContainerList));
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab the checklist item for task list card");
    }
  }

  static class ChecklistCompletedValueHandler implements ValueEventListener
  {
    private Context context;
    private int taskIndex;
    private int cardIndex;
    private long numberOfChecklists;
    private long currentChecklist;
    private long numberOfChecklistItems;
    private long currentChecklistItem;
    private ChecklistCompletionCounter checklistCompletionCounter;
    private List<TaskListModelContainer> taskListModelContainerList;

    ChecklistCompletedValueHandler(Context context,
                                   int taskIndex,
                                   int cardIndex,
                                   long numberOfChecklists,
                                   long currentChecklist,
                                   long numberOfChecklistItems,
                                   long currentChecklistItem,
                                   ChecklistCompletionCounter checklistCompletionCounter,
                                   List<TaskListModelContainer> taskListModelContainerList)
    {
      this.context = context;
      this.taskIndex = taskIndex;
      this.cardIndex = cardIndex;
      this.numberOfChecklists = numberOfChecklists;
      this.currentChecklist = currentChecklist;
      this.numberOfChecklistItems = numberOfChecklistItems;
      this.currentChecklistItem = currentChecklistItem;
      this.checklistCompletionCounter = checklistCompletionCounter;
      this.taskListModelContainerList = taskListModelContainerList;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      // add to the total counter
      ++(checklistCompletionCounter.totalChecklistItems);

      Boolean completedBoolean = dataSnapshot.child(context.getString(R.string.db_checklist_item_completed_key)).getValue(Boolean.class);

      boolean completed = completedBoolean != null ? completedBoolean : false;

      if(completed)
      {
        // add to the complete counter
        ++(checklistCompletionCounter.totalChecked);
      }

      if(currentChecklist == numberOfChecklists &&
              currentChecklistItem == numberOfChecklistItems)
      {
        Log.i("DbProvider", "Create a new checklist completion counter and notify the adapter");

        // We got everything we need. Set the completion counter and notify the adapter
        taskListModelContainerList.get(taskIndex).getChecklistCompletionCounterList().set(cardIndex, checklistCompletionCounter);

        // Notify adapter
        TaskListListAdapter taskListListAdapter = taskListModelContainerList.get(taskIndex).getTaskListListAdapter();
        if(taskListListAdapter != null)
        {
          taskListListAdapter.notifyDataSetChanged();
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab the checklist item data for task list card");
    }
  }

  public static void startChecklistCounterFetch(Context context,
                                                String taskListCardId,
                                                int taskIndex,
                                                int cardIndex,
                                                List<TaskListModelContainer> taskListModelContainerList)
  {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    databaseReference.child(context.getString(R.string.db_task_list_card_checklist_key)).child(taskListCardId)
            .orderByValue()
            .addListenerForSingleValueEvent(new ChecklistIdValueHandler(context,
                    taskIndex,
                    cardIndex,
                    taskListModelContainerList));
  }

  public static void startAttachmentInfoFetch(Context context,
                                              String taskListCardId,
                                              int taskIndex,
                                              int cardIndex,
                                              List<TaskListModelContainer> taskListList)
  {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    databaseReference.child(context.getString(R.string.db_task_list_card_attachment_key))
            .child(taskListCardId)
            .orderByValue()
            .addListenerForSingleValueEvent(new TaskListCardAttachmentIdValueHandler(context,
                    taskListCardId,
                    taskIndex,
                    cardIndex,
                    taskListList));
  }

  static class AttachmentIdValueHandler implements ValueEventListener
  {
    private Context context;
    private String taskListCardId;
    private List<AttachmentCreationInfo> attachmentCreationInfoList;
    private CardDetailAttachmentAdapter attachmentAdapter;
    private IAttachmentFetchListener attachmentFetchListener;
    private IChooseAttachmentDialogListener chooseAttachmentDialogListener;
    long numberOfAttachments;
    long currentAttachment;

    AttachmentIdValueHandler(Context context,
                             String taskListCardId,
                             List<AttachmentCreationInfo> attachmentCreationInfoList,
                             CardDetailAttachmentAdapter attachmentAdapter,
                             IAttachmentFetchListener attachmentFetchListener,
                             IChooseAttachmentDialogListener chooseAttachmentDialogListener)
    {
      this.context = context;
      this.taskListCardId = taskListCardId;
      this.attachmentCreationInfoList = attachmentCreationInfoList;
      this.attachmentAdapter = attachmentAdapter;
      this.attachmentFetchListener = attachmentFetchListener;
      this.chooseAttachmentDialogListener = chooseAttachmentDialogListener;
      numberOfAttachments = 0;
      currentAttachment = 0;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      if(dataSnapshot.hasChildren())
      {
        // we have at least one attachment
        attachmentFetchListener.onAttachment();

        numberOfAttachments = dataSnapshot.getChildrenCount();

        for(DataSnapshot taskListCardAttachmentSnapshot : dataSnapshot.getChildren())
        {
          ++currentAttachment;

          String attachmentId = taskListCardAttachmentSnapshot.getKey();

          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

          databaseReference.child(context.getString(R.string.db_attachment_object))
                  .child(attachmentId)
                  .addListenerForSingleValueEvent(new AttachmentHandler(context,
                          taskListCardId,
                          attachmentId,
                          numberOfAttachments,
                          currentAttachment,
                          attachmentCreationInfoList,
                          attachmentAdapter,
                          chooseAttachmentDialogListener));
        }
      }
      else
      {
        // There are currently no attachments associated with this task list card, so our fetch is
        // complete.

        // Check if we have a delayed attachment callback and trigger the callback.

        if(chooseAttachmentDialogListener != null)
        {
          chooseAttachmentDialogListener.onNewAttachmentDelayed();
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab attachment id from database");
    }
  }

  static class AttachmentHandler implements ValueEventListener
  {
    private Context context;
    private String taskListCardId;
    private String attachmentId;
    private List<AttachmentCreationInfo> attachmentCreationInfoList;
    private CardDetailAttachmentAdapter attachmentAdapter;
    private IChooseAttachmentDialogListener chooseAttachmentDialogListener;
    long numberOfAttachments;
    long currentAttachment;

    AttachmentHandler(Context context,
                      String taskListCardId,
                      String attachmentId,
                      long numberOfAttachments,
                      long currentAttachment,
                      List<AttachmentCreationInfo> attachmentCreationInfoList,
                      CardDetailAttachmentAdapter attachmentAdapter,
                      IChooseAttachmentDialogListener chooseAttachmentDialogListener)
    {
      this.context = context;
      this.taskListCardId = taskListCardId;
      this.attachmentId = attachmentId;
      this.numberOfAttachments = numberOfAttachments;
      this.currentAttachment = currentAttachment;
      this.attachmentCreationInfoList = attachmentCreationInfoList;
      this.attachmentAdapter = attachmentAdapter;
      this.chooseAttachmentDialogListener = chooseAttachmentDialogListener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      String attachmentData = dataSnapshot.child(context.getString(R.string.db_attachment_data_key)).getValue(String.class);

      Long attachmentIndexLong = dataSnapshot.child(context.getString(R.string.db_attachment_index_key)).getValue(Long.class);

      int attachmentIndex = attachmentIndexLong != null ? attachmentIndexLong.intValue() : 0;

      Long attachmentTimeLong = dataSnapshot.child(context.getString(R.string.db_attachment_time_key)).getValue(Long.class);

      long attachmentTime = attachmentTimeLong != null ? attachmentTimeLong : 0;

      Long attachmentTypeLong = dataSnapshot.child(context.getString(R.string.db_attachment_type_key)).getValue(Long.class);

      int attachmentType = attachmentTypeLong != null ? attachmentTypeLong.intValue() : 0;

      Boolean attachmentBoundBoolean = dataSnapshot.child(context.getString(R.string.db_attachment_bound_key)).getValue(Boolean.class);

      Boolean attachmentBound = attachmentBoundBoolean != null ? attachmentBoundBoolean : false;

      AttachmentModel attachmentModel = new AttachmentModel(taskListCardId,
              attachmentId,
              attachmentData,
              attachmentIndex,
              attachmentTime,
              attachmentType,
              attachmentBound);

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      databaseReference.child(context.getString(R.string.db_attachment_extra_data_object))
              .child(attachmentId)
              .addListenerForSingleValueEvent(new AttachmentExtraDataHandler(context,
                      attachmentModel,
                      numberOfAttachments,
                      currentAttachment,
                      attachmentCreationInfoList,
                      attachmentAdapter,
                      chooseAttachmentDialogListener));
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab attachment from database");
    }
  }

  static class AttachmentExtraDataHandler implements ValueEventListener
  {
    private Context context;
    private AttachmentModel attachmentModel;
    private long numberOfAttachments;
    private long currentAttachment;
    private List<AttachmentCreationInfo> attachmentCreationInfoList;
    private CardDetailAttachmentAdapter attachmentAdapter;
    private IChooseAttachmentDialogListener chooseAttachmentDialogListener;

    AttachmentExtraDataHandler(Context context,
                               AttachmentModel attachmentModel,
                               long numberOfAttachments,
                               long currentAttachment,
                               List<AttachmentCreationInfo> attachmentCreationInfoList,
                               CardDetailAttachmentAdapter attachmentAdapter,
                               IChooseAttachmentDialogListener chooseAttachmentDialogListener)
    {
      this.context = context;
      this.attachmentModel = attachmentModel;
      this.numberOfAttachments = numberOfAttachments;
      this.currentAttachment = currentAttachment;
      this.attachmentCreationInfoList = attachmentCreationInfoList;
      this.attachmentAdapter = attachmentAdapter;
      this.chooseAttachmentDialogListener = chooseAttachmentDialogListener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
      if(dataSnapshot.hasChildren())
      {
        String attachmentExtraData = dataSnapshot.child(context.getString(R.string.db_attachment_extra_data_data_key)).getValue(String.class);

        Long attachmentExtraDataTypeLong = dataSnapshot.child(context.getString(R.string.db_attachment_extra_data_type_key)).getValue(Long.class);

        int attachmentExtraDataType = attachmentExtraDataTypeLong != null ? attachmentExtraDataTypeLong.intValue() : 0;

        AttachmentExtraDataModel attachmentExtraDataModel = new AttachmentExtraDataModel(attachmentModel.getCardId(),
                attachmentExtraData,
                attachmentModel.getAttachmentIndex(),
                attachmentExtraDataType);

        AttachmentCreationInfo attachmentCreationInfo = new AttachmentCreationInfo(attachmentModel,
                attachmentExtraDataModel,
                "",
                attachmentModel.getAttachmentType(),
                "",
                attachmentExtraDataModel.getAttachmentExtraType());

        attachmentCreationInfoList.add(attachmentCreationInfo);
      }
      else
      {
        // no extra data
        AttachmentCreationInfo attachmentCreationInfo = new AttachmentCreationInfo(attachmentModel,
                null,
                "",
                attachmentModel.getAttachmentType(),
                "",
                -1);

        attachmentCreationInfoList.add(attachmentCreationInfo);
      }

      if(currentAttachment == numberOfAttachments)
      {
        // we are done fetching attachments

        // notify then handle a delayed attachment if one exists
        attachmentAdapter.notifyDataSetChanged();

        if(chooseAttachmentDialogListener != null)
        {
          chooseAttachmentDialogListener.onNewAttachmentDelayed();
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("DbProvider", "failed to grab attachment extra data from database");
    }
  }

  public static void startAttachmentFetch(Context context,
                                          String taskListCardId,
                                          List<AttachmentCreationInfo> attachmentCreationInfoList,
                                          CardDetailAttachmentAdapter attachmentAdapter,
                                          IAttachmentFetchListener attachmentFetchListener,
                                          IChooseAttachmentDialogListener chooseAttachmentDialogListener)
  {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    databaseReference.child(context.getString(R.string.db_task_list_card_attachment_key))
            .child(taskListCardId)
            .orderByValue()
            .addListenerForSingleValueEvent(new AttachmentIdValueHandler(context,
                    taskListCardId,
                    attachmentCreationInfoList,
                    attachmentAdapter,
                    attachmentFetchListener,
                    chooseAttachmentDialogListener));
  }
}
